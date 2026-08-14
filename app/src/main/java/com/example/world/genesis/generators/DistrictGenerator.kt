package com.example.world.genesis.generators

import com.example.world.genesis.City
import com.example.world.genesis.DistrictType

data class District(val id: Int, val cityId: Int, val radius: Float, val type: DistrictType)

class DistrictGenerator {
    fun generate(city: City): List<District> {
        return listOf(
            District(0, city.id, 30f, DistrictType.Downtown),
            District(1, city.id, 70f, DistrictType.Residential),
            District(2, city.id, 100f, DistrictType.Industrial)
        )
    }
}
