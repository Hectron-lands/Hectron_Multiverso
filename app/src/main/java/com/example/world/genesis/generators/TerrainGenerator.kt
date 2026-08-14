package com.example.world.genesis.generators

import com.example.world.genesis.BiomeType
import com.example.world.genesis.HeightCell
import com.example.world.genesis.WorldMap
import com.example.world.genesis.utils.Noise

class TerrainGenerator {
    fun generate(width: Int, height: Int, seed: Int): WorldMap {
        val noise = Noise(seed)
        val cells = Array(height) { y ->
            Array(width) { x ->
                val h = noise.noise(x / 40f, y / 40f)
                HeightCell(
                    height = h,
                    moisture = 0f,
                    temperature = 0f,
                    biome = BiomeType.Plains
                )
            }
        }
        return WorldMap(width, height, cells)
    }
}
