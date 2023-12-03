package com.example.deezer.Register

data class RegistrarRequest(
    val usuario: String,
    val clave: String,
    val nombre: String,
    val apellidos: String
)