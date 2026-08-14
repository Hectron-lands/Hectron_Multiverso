package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "universes")
data class Universe(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val ownerId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "planets")
data class Planet(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val universeId: String,
    val name: String,
    val type: String, // e.g., rocky, gas, ice, lava
    val description: String,
    val iron: Int = 0,
    val gold: Int = 0,
    val crystal: Int = 0,
    val energy: Int = 0,
    val knowledge: Int = 0,
    val discoveredBy: String,
    val discoveredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "players")
data class Player(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val level: Int = 1,
    val iron: Int = 0,
    val gold: Int = 0,
    val crystal: Int = 0,
    val energy: Int = 0,
    val knowledge: Int = 0,
    val factionId: String? = null
)

@Entity(tableName = "events")
data class UniverseEvent(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val universeId: String,
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "factions")
data class Faction(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val colorHex: String,
    val bonus: String
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val iconName: String
)

@Entity(tableName = "lore")
data class Lore(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val universeId: String,
    val topic: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
