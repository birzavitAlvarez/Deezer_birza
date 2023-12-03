package com.example.deezer.API

import com.example.deezer.Login.LoginRequest
import com.example.deezer.Login.LoginResponse
import com.example.deezer.Musica.MusicResponse
import com.example.deezer.Register.RegisterBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // Login
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/usuarios/registrar")
    suspend fun registerUser(@Body registerBody : RegisterBody)

    @GET("api/music/listar")
    suspend fun listarMusic(): Response<MusicResponse>

}