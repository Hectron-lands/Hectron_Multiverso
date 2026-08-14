package com.example.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// --- API SERVER (Port 3000) ---
interface GameApiService {
    @GET("api/v1/factions")
    suspend fun getFactions(): List<FactionResponse>

    @POST("api/v1/economy/report")
    suspend fun sendEconomyReport(
        @Body report: EconomyReport
    )
}

// --- AUTONOMY SERVER (Port 3001) ---
interface AutonomyApiService {
    @POST("api/v1/telemetry")
    suspend fun sendTelemetry(
        @Body event: TelemetryEvent
    )

    @POST("api/v1/scene/trigger")
    suspend fun triggerBroadcastScene(
        @Body request: AutonomousSceneRequest
    )
}

// --- UNIVERSE SERVER (Port 3002) ---
interface UniverseApiService {
    @GET("api/v1/lore")
    suspend fun getLore(
        @Query("faction") faction: String? = null,
        @Query("limit") limit: Int = 10
    ): List<LoreEntryResponse>

    @POST("api/v1/lore/sync")
    suspend fun syncLocalLore(
        @Body newLore: List<LoreEntryResponse>
    )
}
