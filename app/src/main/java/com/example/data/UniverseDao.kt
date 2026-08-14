package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UniverseDao {
    @Query("SELECT * FROM universes LIMIT 1")
    fun getUniverse(): Flow<Universe?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUniverse(universe: Universe)
    
    @Query("SELECT * FROM planets ORDER BY discoveredAt DESC")
    fun getPlanets(): Flow<List<Planet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanet(planet: Planet)

    @Query("SELECT * FROM players ORDER BY name ASC")
    fun getPlayers(): Flow<List<Player>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)
    
    @Query("SELECT * FROM events ORDER BY timestamp DESC")
    fun getEvents(): Flow<List<UniverseEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: UniverseEvent)

    @Query("SELECT * FROM factions")
    fun getFactions(): Flow<List<Faction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFactions(factions: List<Faction>)

    @Query("SELECT * FROM achievements")
    fun getAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Query("SELECT * FROM lore ORDER BY timestamp DESC")
    fun getLore(): Flow<List<Lore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLore(lore: Lore)
}
