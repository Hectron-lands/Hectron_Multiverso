package com.example.world.genesis.generators

import com.example.world.genesis.City

data class Parcel(
    val id: Int,
    val cityId: Int,
    val x: Float,
    val z: Float,
    val width: Float,
    val depth: Float,
    var occupied: Boolean
)

class ParcelGenerator {
    fun generate(city: City): List<Parcel> {
        val parcels = mutableListOf<Parcel>()
        var id = 0
        val spacing = 18f
        val grid = 18

        for (gz in -grid..grid) {
            for (gx in -grid..grid) {
                parcels.add(
                    Parcel(
                        id = id++,
                        cityId = city.id,
                        x = city.x + gx * spacing,
                        z = city.y + gz * spacing,
                        width = 16f,
                        depth = 16f,
                        occupied = false
                    )
                )
            }
        }
        return parcels
    }
}
