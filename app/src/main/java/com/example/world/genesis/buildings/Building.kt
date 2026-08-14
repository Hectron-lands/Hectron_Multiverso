package com.example.world.genesis.buildings

enum class BuildingType {
    House, Apartment, Store, Restaurant, Factory, School, Hospital, Police, FireStation, Office, Park
}

data class Building(
    val id: Int,
    val type: BuildingType,
    val parcelId: Int,
    val floors: Int,
    val residents: MutableList<Int> = mutableListOf(),
    val workers: MutableList<Int> = mutableListOf(),
    val wealth: Float
)
