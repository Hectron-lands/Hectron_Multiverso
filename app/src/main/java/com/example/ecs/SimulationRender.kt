package com.example.ecs

import com.example.world.genesis.generators.Road
import com.example.world.genesis.generators.RoadNode

data class RenderEntity(
    val id: Int,
    val x: Float,
    val y: Float,
    val isEmployed: Boolean,
    val isStressed: Boolean
)

data class RenderState(
    val entities: List<RenderEntity>,
    val roads: List<Road>,
    val roadNodes: Map<Int, RoadNode>
)
