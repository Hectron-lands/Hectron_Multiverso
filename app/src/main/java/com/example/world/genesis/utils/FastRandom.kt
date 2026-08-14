package com.example.world.genesis.utils

import kotlin.math.abs
import kotlin.math.floor

class FastRandom(private var state: Int) {
    fun next(): Float {
        state = state xor (state shl 13)
        state = state xor (state shr 17)
        state = state xor (state shl 5)
        return (abs(state.toDouble()) / 2147483647.0).toFloat()
    }

    fun range(min: Float, max: Float): Float {
        return min + (max - min) * next()
    }

    fun int(min: Int, max: Int): Int {
        return floor(range(min.toFloat(), max.toFloat())).toInt()
    }
}
