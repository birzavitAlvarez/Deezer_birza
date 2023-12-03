package com.example.deezer.Login

data class LoginResponse(
    val `data`: List<Data>,
    val status: Int,
    val statusMessage: String
)