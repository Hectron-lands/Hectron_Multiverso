package com.example.world.genesis.generators

import kotlin.math.sqrt

data class RoadNode(val id: Int, val x: Float, val y: Float)

data class Road(val from: Int, val to: Int)

class RoadGenerator {
    val roads = mutableListOf<Road>()

    fun connect(nodes: List<RoadNode>): List<Road> {
        roads.clear()

        for (i in 1 until nodes.size) {
            var nearest = 0
            var best = Float.MAX_VALUE

            for (j in 0 until i) {
                val dx = nodes[i].x - nodes[j].x
                val dy = nodes[i].y - nodes[j].y
                val d = sqrt(dx * dx + dy * dy)

                if (d < best) {
                    best = d
                    nearest = j
                }
            }

            roads.add(Road(from = nodes[i].id, to = nodes[nearest].id))
        }

        return roads
    }
}
