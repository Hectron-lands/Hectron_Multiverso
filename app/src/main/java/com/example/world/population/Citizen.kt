package com.example.world.population

enum class Gender {
    Male, Female
}

data class Citizen(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val gender: Gender,
    val buildingId: Int,
    var companyId: Int? = null,
    var money: Float,
    var hunger: Float,
    var energy: Float,
    var happiness: Float
)
