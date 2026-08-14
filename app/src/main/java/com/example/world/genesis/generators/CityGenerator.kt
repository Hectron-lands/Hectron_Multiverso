package com.example.world.genesis.generators

import com.example.world.genesis.BiomeType
import com.example.world.genesis.City
import com.example.world.genesis.WorldMap
import kotlin.random.Random

class CityGenerator {
    private val names = listOf(
        "Nova", "Aurora", "Helix", "Genesis",
        "Elysium", "Arcadia", "Neo City", "Hectron Prime"
    )

    fun generate(world: WorldMap, count: Int = 8): List<City> {
        val cities = mutableListOf<City>()
        var id = 0

        while (cities.size < count) {
            val x = Random.nextInt(world.width)
            val y = Random.nextInt(world.height)
            val cell = world.cells[y][x]

            if (cell.biome == BiomeType.Ocean || cell.biome == BiomeType.Mountains) continue

            cities.add(
                City(
                    id = id++,
                    name = names[id % names.size],
                    x = x.toFloat(),
                    y = y.toFloat(),
                    population = 500 + Random.nextInt(5000)
                )
            )
        }

        return cities
    }
}
