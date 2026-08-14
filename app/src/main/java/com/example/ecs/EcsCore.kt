package com.example.ecs

typealias Entity = Int

object EntityManager {
    private var nextEntity: Entity = 1
    fun createEntity(): Entity = nextEntity++
}

class ComponentStore<T> {
    private val data = mutableMapOf<Entity, T>()
    
    fun add(entity: Entity, component: T) {
        data[entity] = component
    }
    
    fun remove(entity: Entity) {
        data.remove(entity)
    }
    
    fun get(entity: Entity): T? = data[entity]
    
    fun has(entity: Entity): Boolean = data.containsKey(entity)
    
    fun entries(): Set<Map.Entry<Entity, T>> = data.entries
    
    fun values(): Collection<T> = data.values
}
