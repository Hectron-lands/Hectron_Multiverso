package com.example.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TelemetryEvent(
    val timestamp: Long,
    val eventType: String,
    val severity: String,
    val message: String,
    val populationImpacted: Int
)

@JsonClass(generateAdapter = true)
data class LoreEntryResponse(
    val id: String,
    val title: String,
    val content: String,
    val faction: String?,
    val generatedAt: Long
)

@JsonClass(generateAdapter = true)
data class AutonomousSceneRequest(
    val sceneType: String, // HAPPY_SCENE, SAD_SCENE, CRISIS_SCENE
    val reason: String
)

@JsonClass(generateAdapter = true)
data class FactionResponse(
    val id: String,
    val name: String,
    val description: String,
    val bonus: String
)

@JsonClass(generateAdapter = true)
data class EconomyReport(
    val timestamp: Long,
    val totalWealth: Float,
    val unemploymentRate: Float,
    val activeCompanies: Int
)
