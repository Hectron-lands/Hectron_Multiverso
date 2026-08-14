package com.example.ecs

import kotlin.math.max

class MovementSystem {
    fun update(
        transforms: ComponentStore<Transform>,
        velocities: ComponentStore<Velocity>,
        delta: Float
    ) {
        for ((entity, pos) in transforms.entries()) {
            val vel = velocities.get(entity) ?: continue
            pos.x += vel.x * delta
            pos.y += vel.y * delta
            pos.z += vel.z * delta
        }
    }
}

class NeedsSystem {
    fun update(needs: ComponentStore<Needs>, delta: Float) {
        for ((_, n) in needs.entries()) {
            // Decay over time
            n.hunger -= 2.0f * delta
            n.energy -= 1.0f * delta
            n.social -= 0.8f * delta
            n.funLevel -= 1.2f * delta
            n.hygiene -= 0.5f * delta
            n.stress += 0.3f * delta
            
            n.hunger = max(0f, n.hunger)
            n.energy = max(0f, n.energy)
            n.social = max(0f, n.social)
            n.funLevel = max(0f, n.funLevel)
        }
    }
}

class BrainSystem {
    fun update(
        brains: ComponentStore<Brain>,
        needs: ComponentStore<Needs>,
        memories: ComponentStore<Memory>,
        delta: Float
    ) {
        for ((entity, brain) in brains.entries()) {
            val n = needs.get(entity) ?: continue
            val memory = memories.get(entity) ?: continue

            when (brain.state) {
                BrainState.Idle -> {
                    // Stage 1: OBSERVATION & EVALUATION
                    brain.state = BrainState.Thinking
                    
                    // ASTAROTH Verification: Verify internal state reliability
                    val now = System.currentTimeMillis()
                    if (now - brain.verification.lastVerifiedAt > 10000) {
                        verifyInternalState(brain, n, memory)
                    }
                }
                BrainState.Thinking -> {
                    // Stage 2: INTERPRETATION (Interpret needs and social stance)
                    val socialConflict = memory.social.values.sumOf { it.conflictLevel.toDouble() }.toFloat()
                    val interpretedStress = n.stress + (socialConflict * 0.5f)
                    
                    // Stage 3: DECISION & PLANNING
                    if (interpretedStress > 80f) {
                        brain.currentGoal = "Isolate and Recover"
                        brain.state = BrainState.Planning
                    } else if (n.hunger < 30f) {
                        brain.currentGoal = "Acquire Resources"
                        brain.state = BrainState.Planning
                    } else {
                        brain.currentGoal = "Social Interaction"
                        brain.state = BrainState.Planning
                    }
                }
                BrainState.Planning -> {
                    // Stage 4: ACTION SELECTION
                    brain.currentPlan = when (brain.currentGoal) {
                        "Isolate and Recover" -> listOf("Find safe zone", "Rest")
                        "Acquire Resources" -> listOf("Locate market", "Trade")
                        else -> listOf("Find peer", "Exchange information")
                    }
                    
                    // Record in Episodic Memory: Decision taken
                    memory.episodic.add(
                        EpisodicEvent(
                            timestamp = System.currentTimeMillis(),
                            description = "Decided to: ${brain.currentGoal}",
                            importance = 0.6f,
                            source = "BrainOS"
                        )
                    )
                    
                    brain.state = BrainState.Executing
                }
                BrainState.Executing -> {
                    // Stage 5: ACTION & World Change (Logic handled by Movement/Navigation/Economy)
                    // If plan empty, back to idle
                    if (brain.currentPlan.isEmpty()) {
                        brain.state = BrainState.Idle
                    }
                    
                    // Stage 6: IMPACT EVALUATION (Summary logic)
                    // If needs improve, state continues. If not, re-evaluate.
                    if (n.stress < 10f) {
                        brain.currentPlan = emptyList() // Goal achieved
                    }
                }
                else -> { /* Dreaming/Sleeping logic */ }
            }
        }
    }

    private fun verifyInternalState(brain: Brain, needs: Needs, memory: Memory) {
        val contradictions = mutableListOf<String>()
        
        // Example ASTAROTH check: Does episodic memory contradict current goals?
        val lastEvent = memory.episodic.lastOrNull()
        if (lastEvent != null && lastEvent.description.contains("failed") && brain.currentGoal == lastEvent.description.substringAfter("to: ")) {
            contradictions.add("Attempting repeated failed goal: ${brain.currentGoal}")
        }
        
        brain.verification.lastVerifiedAt = System.currentTimeMillis()
        brain.verification.contradictionsFound = contradictions
        brain.verification.reliabilityScore = if (contradictions.isEmpty()) 1.0f else 0.4f
    }
}

class MetaBrainSystem {
    private var timeSinceLastEvent = 0f
    
    fun update(
        needs: ComponentStore<Needs>,
        delta: Float,
        onEventEmitted: (String) -> Unit
    ) {
        timeSinceLastEvent += delta
        
        // Analyze the macro state every few ticks to trigger emergent phenomena
        if (timeSinceLastEvent > 5f) {
            var totalHunger = 0f
            var totalEnergy = 0f
            var count = 0
            
            for ((_, n) in needs.entries()) {
                totalHunger += n.hunger
                totalEnergy += n.energy
                count++
            }
            
            if (count > 0) {
                val avgHunger = totalHunger / count
                val avgEnergy = totalEnergy / count
                
                if (avgHunger < 30f) {
                    onEventEmitted("META-EMERGENCE: Food shortage detected in Sector 7. Smuggler markets organically arising.")
                    // Intervene slightly to simulate emergent supply
                    for ((_, n) in needs.entries()) n.hunger += 40f 
                } else if (avgEnergy > 80f && avgHunger > 60f) {
                     onEventEmitted("META-EMERGENCE: High surplus of energy and resources. A spontaneous festival has started!")
                     for ((_, n) in needs.entries()) {
                         n.energy -= 20f
                         n.funLevel += 50f
                     }
                } else if (avgHunger < 60f && avgEnergy < 60f) {
                     onEventEmitted("META-EMERGENCE: Rising stress levels. Citizens are migrating towards industrial zones seeking jobs.")
                     for ((_, n) in needs.entries()) {
                         n.stress += 10f
                     }
                }
            }
            timeSinceLastEvent = 0f
        }
    }
}

class NavigationSystem {
    fun update(
        transforms: ComponentStore<Transform>,
        navigations: ComponentStore<Navigation>,
        velocities: ComponentStore<Velocity>,
        delta: Float,
        nodes: Map<Int, com.example.world.genesis.generators.RoadNode>
    ) {
        val speed = 10f
        for ((entity, nav) in navigations.entries()) {
            if (nav.currentPath.isEmpty() || nav.currentTargetIndex >= nav.currentPath.size) {
                // Reached destination
                velocities.get(entity)?.x = 0f
                velocities.get(entity)?.y = 0f
                continue
            }

            val targetNodeId = nav.currentPath[nav.currentTargetIndex]
            val targetNode = nodes[targetNodeId] ?: continue
            
            val transform = transforms.get(entity) ?: continue
            val vel = velocities.get(entity) ?: continue

            val dx = targetNode.x - transform.x
            val dy = targetNode.y - transform.y
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)

            if (dist < 2f) {
                // Reached node
                nav.currentTargetIndex++
            } else {
                vel.x = (dx / dist) * speed
                vel.y = (dy / dist) * speed
            }
        }
    }
}

class EconomySystem {
    private var timeSinceLastTick = 0f
    var currentMarket = Market()

    fun update(
        inventories: ComponentStore<Inventory>,
        professions: ComponentStore<Profession>,
        needs: ComponentStore<Needs>,
        delta: Float,
        onReportEmitted: (Float, Float, Int) -> Unit
    ) {
        timeSinceLastTick += delta
        if (timeSinceLastTick > 10f) { // Every 10 seconds of simulation
            var totalWealth = 0f
            var employedCount = 0
            
            for ((entity, inventory) in inventories.entries()) {
                val prof = professions.get(entity)
                if (prof != null) {
                    // Pay salary
                    inventory.money += prof.salary
                    employedCount++
                    
                    // Produce resources based on profession
                    when (prof.producingResource) {
                        "iron" -> inventory.iron += 2f
                        "gold" -> inventory.gold += 0.5f
                        "crystal" -> inventory.crystal += 0.2f
                        "energy" -> inventory.energy += 10f
                        "knowledge" -> inventory.knowledge += 0.1f
                    }
                }
                
                // Automatic Trading: Sell excess resources to the market
                if (inventory.iron > 5f) {
                    inventory.money += inventory.iron * currentMarket.ironPrice
                    inventory.iron = 0f
                }
                if (inventory.gold > 1f) {
                    inventory.money += inventory.gold * currentMarket.goldPrice
                    inventory.gold = 0f
                }
                
                val n = needs.get(entity)
                if (n != null) {
                    // Economy affects needs. If you have money, you can buy food and reduce stress.
                    if (inventory.money > 10f && n.hunger < 50f) {
                        inventory.money -= 10f
                        n.hunger += 30f // Bought food
                    }
                    if (inventory.money < 20f) {
                        n.stress += 5f // Poverty causes stress
                    } else {
                        n.stress -= 2f // Having money reduces stress
                    }
                }
                
                totalWealth += inventory.money
            }

            val unemploymentRate = if (inventories.entries().isNotEmpty()) {
                1f - (employedCount.toFloat() / inventories.entries().size.toFloat())
            } else 0f
            
            onReportEmitted(totalWealth, unemploymentRate, employedCount)
            timeSinceLastTick = 0f
        }
    }
}
