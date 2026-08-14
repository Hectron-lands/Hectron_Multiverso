package com.example.world.genesis.generators

import com.example.world.genesis.buildings.Building
import com.example.world.genesis.buildings.BuildingType
import kotlin.random.Random

class BuildingGenerator {
    private var id = 0

    fun generate(parcels: List<Parcel>): List<Building> {
        val buildings = mutableListOf<Building>()

        for (parcel in parcels) {
            if (Random.nextFloat() < 0.10f) continue

            val building = Building(
                id = id++,
                parcelId = parcel.id,
                type = randomType(),
                floors = 1 + Random.nextInt(6),
                wealth = 1000f + Random.nextFloat() * 9000f
            )

            parcel.occupied = true
            buildings.add(building)
        }

        return buildings
    }

    private fun randomType(): BuildingType {
        val r = Random.nextFloat()
        return when {
            r < 0.55f -> BuildingType.House
            r < 0.70f -> BuildingType.Apartment
            r < 0.80f -> BuildingType.Store
            r < 0.86f -> BuildingType.Restaurant
            r < 0.92f -> BuildingType.Office
            r < 0.96f -> BuildingType.Factory
            r < 0.98f -> BuildingType.School
            else -> BuildingType.Hospital
        }
    }
}
