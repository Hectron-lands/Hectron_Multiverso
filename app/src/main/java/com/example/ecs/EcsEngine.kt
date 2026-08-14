package com.example.ecs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EcsEngine {
    val transforms = ComponentStore<Transform>()
    val velocities = ComponentStore<Velocity>()
    val needs = ComponentStore<Needs>()
    val brains = ComponentStore<Brain>()
    val navigations = ComponentStore<Navigation>()
    val inventories = ComponentStore<Inventory>()
    val professions = ComponentStore<Profession>()
    val memories = ComponentStore<Memory>()
    
    private val movementSystem = MovementSystem()
    private val needsSystem = NeedsSystem()
    private val brainSystem = BrainSystem()
    private val metaBrainSystem = MetaBrainSystem()
    private val navigationSystem = NavigationSystem()
    private val economySystem = EconomySystem()
    
    // For pathfinding
    var roadNodes: Map<Int, com.example.world.genesis.generators.RoadNode> = emptyMap()
    var roads: List<com.example.world.genesis.generators.Road> = emptyList()
    
    private val _engineLog = MutableStateFlow<List<String>>(emptyList())
    val engineLog: StateFlow<List<String>> = _engineLog.asStateFlow()
    
    private val _profiles = MutableStateFlow<List<EntityProfile>>(emptyList())
    val profiles: StateFlow<List<EntityProfile>> = _profiles.asStateFlow()
    
    private val _renderState = MutableStateFlow<RenderState?>(null)
    val renderState: StateFlow<RenderState?> = _renderState.asStateFlow()
    
    private var simulationJob: Job? = null
    
    fun initializePopulation(count: Int) {
        logEvent("Initializing World with Genesis Engine...")
        val genesis = com.example.world.genesis.GenesisEngine()
        val worldData = genesis.generateWorld()
        
        logEvent("Generated ${worldData.cities.size} cities, ${worldData.buildings.size} buildings, and ${worldData.citizens.size} citizens.")
        
        // Populate pathfinding network
        val nodesMap = mutableMapOf<Int, com.example.world.genesis.generators.RoadNode>()
        worldData.cities.forEach { city ->
            nodesMap[city.id] = com.example.world.genesis.generators.RoadNode(city.id, city.x, city.y)
        }
        this.roadNodes = nodesMap
        this.roads = worldData.roads

        // We'll limit to a subset of citizens for the UI performance if there are too many, but ECS can handle them.
        // Let's cap at 50 for the UI visualization limit.
        val citizensToSimulate = worldData.citizens.take(50)
        
        for (citizen in citizensToSimulate) {
            val entity = EntityManager.createEntity()
            transforms.add(entity, Transform((0..100).random().toFloat(), (0..100).random().toFloat(), 0f, 0f))
            velocities.add(entity, Velocity(0f, 0f, 0f))
            
            // Map Genesis Citizen data to ECS Components
            needs.add(entity, Needs(citizen.hunger, 100f, citizen.energy, 100f, 100f, citizen.happiness, 0f))
            brains.add(entity, Brain("Idle", emptyList()))
            memories.add(entity, Memory())
            
            // Assign Economy components
            inventories.add(entity, Inventory(citizen.money))
            if (citizen.companyId != null) {
                val resource = listOf("iron", "gold", "energy", "crystal").random()
                professions.add(entity, Profession(citizen.companyId!!, 500f, "Worker", 1f, resource))
            }
            
            // Add Navigation component
            navigations.add(entity, Navigation(emptyList(), 0))
            
            // We can store the name in the ECS somehow, but for now we'll just update profiles with it.
            // A NameComponent would be better, but we can hack it via a map for the UI.
            entityNames[entity] = "${citizen.firstName} ${citizen.lastName}"
        }
        updateProfiles()
    }
    
    private val entityNames = mutableMapOf<Entity, String>()
    
    fun startSimulation() {
        if (simulationJob?.isActive == true) return
        logEvent("Simulation started.")
        
        simulationJob = CoroutineScope(Dispatchers.Default).launch {
            var lastTime = System.currentTimeMillis()
            while (true) {
                val currentTime = System.currentTimeMillis()
                val delta = (currentTime - lastTime) / 1000f // in seconds
                lastTime = currentTime
                
                tick(delta)
                
                delay(100L) // 10 ticks per second
            }
        }
    }
    
    fun stopSimulation() {
        simulationJob?.cancel()
        logEvent("Simulation paused.")
    }
    
    private var uiUpdateTimer = 0L
    private fun tick(delta: Float) {
        movementSystem.update(transforms, velocities, delta)
        needsSystem.update(needs, delta)
        brainSystem.update(brains, needs, memories, delta)
        navigationSystem.update(transforms, navigations, velocities, delta, roadNodes)
        
        economySystem.update(inventories, professions, needs, delta) { wealth, unemployment, employed ->
            logEvent("ECONOMY REPORT: Global Wealth: $wealth, Unemployment: ${unemployment * 100}%")
            
            // Trigger Telemetry if unemployment is critical
            if (unemployment > 0.5f) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        com.example.network.NetworkModule.gameApi.sendEconomyReport(
                            com.example.network.EconomyReport(
                                timestamp = System.currentTimeMillis(),
                                totalWealth = wealth,
                                unemploymentRate = unemployment,
                                activeCompanies = employed
                            )
                        )
                        com.example.network.NetworkModule.autonomyApi.sendTelemetry(
                            com.example.network.TelemetryEvent(
                                timestamp = System.currentTimeMillis(),
                                eventType = "ECONOMIC_CRASH",
                                severity = "CRITICAL",
                                message = "Unemployment reached ${unemployment * 100}%",
                                populationImpacted = employed
                            )
                        )
                    } catch (e: Exception) {}
                }
            }
        }
        
        metaBrainSystem.update(needs, delta) { eventMsg ->
            logEvent(eventMsg)
            // Fire telemetry event to Autonomy Node.js Server
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    com.example.network.NetworkModule.autonomyApi.sendTelemetry(
                        com.example.network.TelemetryEvent(
                            timestamp = System.currentTimeMillis(),
                            eventType = "METABRAIN_EMERGENCE",
                            severity = "WARNING",
                            message = eventMsg,
                            populationImpacted = needs.entries().size
                        )
                    )
                } catch (e: Exception) {
                    // Fail silently if backend is offline to prevent crashing the Android simulation
                }
            }
        }
        
        // Update UI state occasionally
        val now = System.currentTimeMillis()
        if (now - uiUpdateTimer > 100) {
            updateProfiles()
            updateRenderState()
            uiUpdateTimer = now
        }
    }
    
    private fun updateRenderState() {
        val entities = mutableListOf<RenderEntity>()
        for ((entity, transform) in transforms.entries()) {
            val prof = professions.get(entity)
            val n = needs.get(entity)
            entities.add(
                RenderEntity(
                    id = entity,
                    x = transform.x,
                    y = transform.y,
                    isEmployed = prof != null,
                    isStressed = (n?.stress ?: 0f) > 50f
                )
            )
        }
        _renderState.value = RenderState(entities, roads, roadNodes)
    }
    
    private fun updateProfiles() {
        val list = mutableListOf<EntityProfile>()
        for ((entity, _) in brains.entries()) {
            list.add(
                EntityProfile(
                    id = entity,
                    name = entityNames[entity] ?: "Citizen #$entity",
                    brain = brains.get(entity)?.copy(),
                    needs = needs.get(entity)?.copy()
                )
            )
        }
        _profiles.value = list
    }
    
    private fun logEvent(msg: String) {
        val current = _engineLog.value.toMutableList()
        current.add(0, msg)
        if (current.size > 20) current.removeLast()
        _engineLog.value = current
    }
}
