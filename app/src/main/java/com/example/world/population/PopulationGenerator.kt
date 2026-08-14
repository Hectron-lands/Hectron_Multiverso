package com.example.world.population

import com.example.world.genesis.buildings.Building
import com.example.world.genesis.buildings.BuildingType
import kotlin.random.Random

class PopulationGenerator {
    private var id = 0

    private val firstNames = listOf(
        "Alex", "Daniel", "Sofía", "Emma",
        "Luis", "Carlos", "Mateo", "Olivia",
        "Valeria", "Noah", "Elena", "Lucas"
    )

    private val lastNames = listOf(
        "Ruiz", "Smith", "Miller", "Garcia",
        "Johnson", "Brown", "Martinez", "Lee"
    )

    fun generate(buildings: List<Building>): List<Citizen> {
        val citizens = mutableListOf<Citizen>()

        for (b in buildings) {
            if (b.type != BuildingType.House && b.type != BuildingType.Apartment) continue

            val people = 1 + Random.nextInt(5)

            for (i in 0 until people) {
                val citizen = Citizen(
                    id = id++,
                    firstName = firstNames.random(),
                    lastName = lastNames.random(),
                    gender = if (Random.nextFloat() < 0.5f) Gender.Male else Gender.Female,
                    age = 18 + Random.nextInt(70),
                    buildingId = b.id,
                    companyId = null,
                    money = 500f + Random.nextFloat() * 3000f,
                    hunger = 100f,
                    energy = 100f,
                    happiness = 100f
                )

                citizens.add(citizen)
                b.residents.add(citizen.id)
            }
        }
        return citizens
    }
}
