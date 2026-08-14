package com.example.world.genesis.generators

import com.example.world.genesis.BiomeType
import com.example.world.genesis.WorldMap
import kotlin.random.Random

data class RiverNode(val x: Int, val y: Int)

class RiverGenerator {
    fun generate(world: WorldMap, rivers: Int = 20) {
        for (i in 0 until rivers) {
            val source = findMountain(world) ?: continue
            flow(world, source)
        }
    }

    private fun findMountain(world: WorldMap): RiverNode? {
        for (k in 0 until 5000) {
            val x = Random.nextInt(world.width)
            val y = Random.nextInt(world.height)
            val cell = world.cells[y][x]
            if (cell.biome == BiomeType.Mountains) {
                return RiverNode(x, y)
            }
        }
        return null
    }

    private fun flow(world: WorldMap, node: RiverNode) {
        var current = node
        var iterations = 0

        while (iterations < 800) {
            iterations++
            val cell = world.cells[current.y][current.x]
            cell.moisture = 1f

            var next = current
            var lowest = cell.height

            for (oy in -1..1) {
                for (ox in -1..1) {
                    if (ox == 0 && oy == 0) continue

                    val nx = current.x + ox
                    val ny = current.y + oy

                    if (nx < 0 || ny < 0 || nx >= world.width || ny >= world.height) continue

                    val n = world.cells[ny][nx]
                    if (n.height < lowest) {
                        lowest = n.height
                        next = RiverNode(nx, ny)
                    }
                }
            }

            if (next == current) break
            current = next
            if (world.cells[current.y][current.x].biome == BiomeType.Ocean) break
        }
    }
}
