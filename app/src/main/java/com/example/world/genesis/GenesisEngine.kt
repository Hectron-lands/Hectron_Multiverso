package com.example.world.genesis

import com.example.world.genesis.buildings.Building
import com.example.world.genesis.generators.BuildingGenerator
import com.example.world.genesis.generators.Parcel
import com.example.world.genesis.generators.ParcelGenerator
import com.example.world.genesis.generators.TerrainGenerator
import com.example.world.genesis.generators.BiomeGenerator
import com.example.world.genesis.generators.RiverGenerator
import com.example.world.genesis.generators.RoadGenerator
import com.example.world.genesis.generators.CityGenerator
import com.example.world.genesis.generators.Road
import com.example.world.genesis.generators.RoadNode

import com.example.world.economy.Company
import com.example.world.economy.CompanyGenerator
import com.example.world.population.Citizen
import com.example.world.population.PopulationGenerator

data class WorldData(
    val worldMap: WorldMap,
    val cities: List<City>,
    val roads: List<Road>,
    val parcels: List<Parcel>,
    val buildings: List<Building>,
    val citizens: List<Citizen>,
    val companies: List<Company>
)

class GenesisEngine {
    private val terrain = TerrainGenerator()
    private val biomes = BiomeGenerator()
    private val rivers = RiverGenerator()
    private val citiesGen = CityGenerator()
    private val roads = RoadGenerator()
    
    private val parcelGenerator = ParcelGenerator()
    private val buildingGenerator = BuildingGenerator()
    private val populationGenerator = PopulationGenerator()
    private val companyGenerator = CompanyGenerator()

    fun generateWorld(seedValue: Int = 12345): WorldData {
        val seed = WorldSeed(seedValue)
        
        // Phase 1: Macro World
        val world = terrain.generate(seed.width, seed.height, seed.seed)
        biomes.apply(world)
        rivers.generate(world)
        
        val cityList = citiesGen.generate(world)
        val roadNodes = cityList.map { RoadNode(it.id, it.x, it.y) }
        val roadNetwork = roads.connect(roadNodes)

        // Phase 2: Micro World (Pick the first city for deep simulation to avoid OOM)
        val targetCity = cityList.firstOrNull() ?: City(id = 1, name = "New Hectron City", x = 0f, y = 0f, population = 1000)

        val parcels = parcelGenerator.generate(targetCity)
        val buildings = buildingGenerator.generate(parcels)
        val citizens = populationGenerator.generate(buildings)
        val companies = companyGenerator.generate(buildings)

        return WorldData(
            worldMap = world,
            cities = cityList,
            roads = roadNetwork,
            parcels = parcels,
            buildings = buildings,
            citizens = citizens,
            companies = companies
        )
    }
}
