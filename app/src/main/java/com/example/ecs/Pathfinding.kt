package com.example.ecs

import com.example.world.genesis.generators.RoadNode
import com.example.world.genesis.generators.Road
import java.util.PriorityQueue

data class PathNode(val id: Int, val x: Float, val y: Float)

object AStarPathfinding {

    fun findPath(
        startId: Int,
        targetId: Int,
        nodes: List<RoadNode>,
        roads: List<Road>
    ): List<Int> {
        val adjacencyList = mutableMapOf<Int, MutableList<Int>>()
        
        for (road in roads) {
            adjacencyList.getOrPut(road.from) { mutableListOf() }.add(road.to)
            adjacencyList.getOrPut(road.to) { mutableListOf() }.add(road.from)
        }

        val nodeMap = nodes.associateBy { it.id }

        if (!nodeMap.containsKey(startId) || !nodeMap.containsKey(targetId)) return emptyList()

        val openSet = PriorityQueue<AStarNode>(compareBy { it.fCost })
        val closedSet = mutableSetOf<Int>()
        val gCosts = mutableMapOf<Int, Float>()
        val parentMap = mutableMapOf<Int, Int>()

        val startNode = nodeMap[startId]!!
        val targetNode = nodeMap[targetId]!!

        gCosts[startId] = 0f
        openSet.add(AStarNode(startId, 0f, heuristic(startNode, targetNode)))

        while (openSet.isNotEmpty()) {
            val current = openSet.poll()

            if (current.id == targetId) {
                return reconstructPath(parentMap, current.id)
            }

            closedSet.add(current.id)

            val neighbors = adjacencyList[current.id] ?: emptyList()
            for (neighborId in neighbors) {
                if (closedSet.contains(neighborId)) continue

                val neighborNode = nodeMap[neighborId] ?: continue
                val currentNode = nodeMap[current.id]!!
                val tentativeGCost = gCosts[current.id]!! + heuristic(currentNode, neighborNode)

                if (tentativeGCost < (gCosts[neighborId] ?: Float.MAX_VALUE)) {
                    parentMap[neighborId] = current.id
                    gCosts[neighborId] = tentativeGCost
                    val hCost = heuristic(neighborNode, targetNode)
                    
                    // Simple priority queue update logic: just add it, duplicate isn't ideal but works for simple graphs
                    openSet.add(AStarNode(neighborId, tentativeGCost, hCost))
                }
            }
        }
        return emptyList()
    }

    private fun heuristic(a: RoadNode, b: RoadNode): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    private fun reconstructPath(parentMap: Map<Int, Int>, current: Int): List<Int> {
        val path = mutableListOf(current)
        var curr = current
        while (parentMap.containsKey(curr)) {
            curr = parentMap[curr]!!
            path.add(0, curr)
        }
        return path
    }

    private data class AStarNode(val id: Int, val gCost: Float, val hCost: Float) {
        val fCost: Float get() = gCost + hCost
    }
}
