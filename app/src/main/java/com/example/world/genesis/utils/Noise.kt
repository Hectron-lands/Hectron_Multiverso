package com.example.world.genesis.utils

import kotlin.math.floor
import kotlin.math.sin

class Noise(seed: Int) {
    private val random = FastRandom(seed)

    fun noise(x: Float, y: Float): Float {
        val n = sin(x * 12.9898 + y * 78.233 + random.next() * 43758.5453).toFloat()
        return n - floor(n)
    }
}
