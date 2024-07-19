package com.example.pair3

data class GameData(
    val gameId: String = "",
    val images: List<Any> = emptyList(),
    val players: Map<String, Boolean> = emptyMap(),
    val status: String = ""
)