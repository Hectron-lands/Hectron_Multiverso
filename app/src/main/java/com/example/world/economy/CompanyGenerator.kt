package com.example.world.economy

import com.example.world.genesis.buildings.Building
import com.example.world.genesis.buildings.BuildingType

class CompanyGenerator {
    private var id = 0

    fun generate(buildings: List<Building>): List<Company> {
        val companies = mutableListOf<Company>()

        for (b in buildings) {
            if (b.type == BuildingType.Store ||
                b.type == BuildingType.Office ||
                b.type == BuildingType.Factory ||
                b.type == BuildingType.Restaurant
            ) {
                companies.add(
                    Company(
                        id = id++,
                        name = "Company-$id",
                        buildingId = b.id,
                        capital = 50000f,
                        production = 100f
                    )
                )
            }
        }
        return companies
    }
}
