package com.example.pair3

data class User(
    var userName: String = "",
    var score: Int = 0,
    var userTimePlayed: String = "",
    var PhotoUrl: String = ""
) {
    // Constructeur par défaut vide nécessaire pour Firebase
    constructor() : this("", 0, "", "")
}
