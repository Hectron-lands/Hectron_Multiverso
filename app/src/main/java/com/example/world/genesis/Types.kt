package com.example.world.genesis

enum class BiomeType {
    Ocean, Beach, Plains, Forest, Jungle, Desert, Mountains, Snow
}

enum class DistrictType {
    Residential, Commercial, Industrial, Downtown, Rural
}

data class HeightCell(
    var height: Float,
    var moisture: Float,
    var temperature: Float,
    var biome: BiomeType
)

class WorldMap(
    val width: Int,
    val height: Int,
    val cells: Array<Array<HeightCell>>
)

class WorldSeed(
    val seed: Int,
    val width: Int = 512,
    val height: Int = 512
)
