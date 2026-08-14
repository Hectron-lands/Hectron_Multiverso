package com.example.world.genesis.generators

import com.example.world.genesis.BiomeType
import com.example.world.genesis.WorldMap

class BiomeGenerator {
    fun apply(world: WorldMap) {
        for (y in 0 until world.height) {
            for (x in 0 until world.width) {
                val cell = world.cells[y][x]
                when {
                    cell.height < 0.20f -> cell.biome = BiomeType.Ocean
                    cell.height < 0.25f -> cell.biome = BiomeType.Beach
                    cell.height < 0.55f -> cell.biome = BiomeType.Plains
                    cell.height < 0.75f -> cell.biome = BiomeType.Forest
                    else -> cell.biome = BiomeType.Mountains
                }
            }
        }
    }
}
