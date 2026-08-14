package com.example.world.economy

data class Company(
    val id: Int,
    val name: String,
    val buildingId: Int,
    val employees: MutableList<Int> = mutableListOf(),
    val capital: Float,
    val production: Float
)
