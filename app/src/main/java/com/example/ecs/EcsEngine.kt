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

    private val _telemetry = MutableStateFlow(EngineTelemetry())
    val telemetry: StateFlow<EngineTelemetry> = _telemetry.asStateFlow()
    
    private var simulationJob: Job? = null
    private val entityNames = mutableMapOf<Entity, String>()
    
    fun initializePopulation(count: Int) {
        logEvent("Initializing World with Genesis Engine (Optimized)...")
        val genesis = com.example.world.genesis.GenesisEngine()
        val worldData = genesis.generateWorld()
        
        logEvent("Generated ${worldData.cities.size} cities, ${worldData.buildings.size} buildings, and ${worldData.citizens.size} citizens.")
        
        val nodesMap = mutableMapOf<Int, com.example.world.genesis.generators.RoadNode>()
        worldData.cities.forEach { city ->
            nodesMap[city.id] = com.example.world.genesis.generators.RoadNode(city.id, city.x, city.y)
        }
        this.roadNodes = nodesMap
        this.roads = worldData.roads

        val citizensToSimulate = worldData.citizens.take(50)
        
        for (citizen in citizensToSimulate) {
            val entity = EntityManager.createEntity()
            transforms.add(entity, Transform((0..100).random().toFloat(), (0..100).random().toFloat(), 0f, 0f))
            velocities.add(entity, Velocity(0f, 0f, 0f))
            
            needs.add(entity, Needs(citizen.hunger, 100f, citizen.energy, 100f, 100f, citizen.happiness, 0f))
            brains.add(entity, Brain("Idle", emptyList()))
            memories.add(entity, Memory())
            
            inventories.add(entity, Inventory(citizen.money))
            if (citizen.companyId != null) {
                val resource = listOf("iron", "gold", "energy", "crystal").random()
                professions.add(entity, Profession(citizen.companyId!!, 500f, "Worker", 1f, resource))
            }
            
            navigations.add(entity, Navigation(emptyList(), 0))
            entityNames[entity] = "${citizen.firstName} ${citizen.lastName}"
        }
        updateProfiles()
    }
    
    fun startSimulation() {
        if (simulationJob?.isActive == true) return
        logEvent("Optimized simulation loop started (Target: 60 FPS / 16ms tick budget).")
        
        simulationJob = CoroutineScope(Dispatchers.Default).launch {
            var lastTime = System.currentTimeMillis()
            var frameCount = 0
            var fpsTimer = System.currentTimeMillis()
            
            while (true) {
                val currentTime = System.currentTimeMillis()
                val delta = (currentTime - lastTime) / 1000f
                lastTime = currentTime
                
                val tickStart = System.nanoTime()
                tick(delta)
                val tickDurationMs = (System.nanoTime() - tickStart) / 1_000_000f

                frameCount++
                if (currentTime - fpsTimer >= 1000L) {
                    val currentFps = (frameCount * 1000f) / (currentTime - fpsTimer)
                    val activeCount = transforms.entries().size
                    
                    // ASTAROTH avg reliability across brains
                    var totalRel = 0f
                    var brainCount = 0
                    for ((_, b) in brains.entries()) {
                        totalRel += b.verification.reliabilityScore
                        brainCount++
                    }
                    val avgRel = if (brainCount > 0) totalRel / brainCount else 1.0f

                    _telemetry.value = EngineTelemetry(
                        fps = (currentFps * 10).toInt() / 10f,
                        tickDurationMs = (tickDurationMs * 100).toInt() / 100f,
                        activeEntities = activeCount,
                        astarothReliability = (avgRel * 100).toInt() / 100f,
                        memoryFootprintMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024f * 1024f),
                        sovereigntyLevel = 4
                    )
                    frameCount = 0
                    fpsTimer = currentTime
                }
                
                delay(16L) // ~60 FPS smooth tick rate
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
                } catch (e: Exception) {}
            }
        }
        
        val now = System.currentTimeMillis()
        if (now - uiUpdateTimer > 66) { // Throttled ~15 Hz UI sync to save CPU & avoid GC thrashing
            updateProfiles()
            updateRenderState()
            uiUpdateTimer = now
        }
    }
    
    private fun updateRenderState() {
        val entries = transforms.entries()
        val entities = ArrayList<RenderEntity>(entries.size)
        for ((entity, transform) in entries) {
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
        val brainEntries = brains.entries()
        val list = ArrayList<EntityProfile>(brainEntries.size)
        for ((entity, brain) in brainEntries) {
            list.add(
                EntityProfile(
                    id = entity,
                    name = entityNames[entity] ?: "Citizen #$entity",
                    brain = brain.copy(),
                    needs = needs.get(entity)?.copy()
                )
            )
        }
        _profiles.value = list
    }
    
    private fun logEvent(msg: String) {
        val current = _engineLog.value.toMutableList()
        current.add(0, msg)
        if (current.size > 25) current.removeLast()
        _engineLog.value = current
    }
}
