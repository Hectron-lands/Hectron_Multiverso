package com.example.ecs

data class Transform(var x: Float, var y: Float, var z: Float, var rotation: Float)

data class Velocity(var x: Float, var y: Float, var z: Float)

data class Needs(
    var hunger: Float,
    var thirst: Float,
    var energy: Float,
    var hygiene: Float,
    var social: Float,
    var funLevel: Float,
    var stress: Float
)

data class Brain(
    var currentGoal: String,
    var currentPlan: List<String>,
    var state: BrainState = BrainState.Idle,
    var verification: AstarothVerification = AstarothVerification()
)

data class AstarothVerification(
    var lastVerifiedAt: Long = 0,
    var reliabilityScore: Float = 1.0f,
    var contradictionsFound: List<String> = emptyList()
)

enum class BrainState {
    Idle, Thinking, Planning, Executing, Sleeping, Dreaming
}

data class Memory(
    val episodic: MutableList<EpisodicEvent> = mutableListOf(),
    val semantic: MutableMap<String, String> = mutableMapOf(),
    val procedural: MutableList<String> = mutableListOf(),
    val social: MutableMap<Entity, SocialStance> = mutableMapOf()
)

data class EpisodicEvent(
    val timestamp: Long,
    val description: String,
    val importance: Float,
    val source: String
)

data class SocialStance(
    var trust: Float,
    var respect: Float,
    var structuralPower: Float,
    var conflictLevel: Float
)

data class Inventory(
    var money: Float,
    var iron: Float = 0f,
    var gold: Float = 0f,
    var crystal: Float = 0f,
    var energy: Float = 0f,
    var knowledge: Float = 0f,
    val items: MutableList<Int> = mutableListOf()
)

data class Market(
    val ironPrice: Float = 1f,
    val goldPrice: Float = 5f,
    val crystalPrice: Float = 10f,
    val energyPrice: Float = 2f,
    val knowledgePrice: Float = 3f
)

data class Profession(
    var company: Entity,
    var salary: Float,
    var role: String,
    var experience: Float,
    var producingResource: String? = null
)

data class Navigation(
    var currentPath: List<Int> = emptyList(),
    var currentTargetIndex: Int = 0
)

data class EntityProfile(
    val id: Entity,
    val name: String,
    val brain: Brain?,
    val needs: Needs?
)

data class EngineTelemetry(
    val fps: Float = 60.0f,
    val tickDurationMs: Float = 1.2f,
    val activeEntities: Int = 0,
    val astarothReliability: Float = 1.0f,
    val memoryFootprintMb: Float = 12.4f,
    val sovereigntyLevel: Int = 4
)
