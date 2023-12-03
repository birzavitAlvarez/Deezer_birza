package com.example.deezer.Login


import java.io.Serializable

data class UserData(
    val id_usuarios: Int,
    val nombre: String,
    val apellidos: String,
    val usuario: String
) : Serializable