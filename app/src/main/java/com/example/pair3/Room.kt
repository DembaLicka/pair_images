package com.example.pair3

data class Room(
    val id: String = "",
    val name: String = "",
    val creator: String = "",
    val nomduproprio: String = "",
    val players: Map<String, Boolean> = emptyMap(),
    val images: List<Int>? = null,
    var deuxjoueurs : Int = 0,
    var gameState: String = "waiting" // Initialisation par défaut à "waiting"

)
