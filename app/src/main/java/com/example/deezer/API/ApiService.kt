package com.example.deezer.API

import com.example.deezer.Favoritos.FavoritosResponse
import com.example.deezer.Login.LoginRequest
import com.example.deezer.Login.LoginResponse
import com.example.deezer.Register.RegisterBody
import com.example.deezer.Musica.MusicResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Login
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/usuarios/registrar")
    suspend fun registerUser(@Body registerBody : RegisterBody)

    @GET("api/music/listmusic")
    suspend fun listarMusic(): Response<MusicResponse>

    // Filtrar por razon_social
    @GET("api/music/busqueda")
    suspend fun buscarMusic(@Query("cancion") cancion: String): Response<MusicResponse>

    // lista favoritos
    @GET("api/favoritos/listafavoritos/{id}")
    suspend fun listFavoritos(@Path("id") id: Int): Response<FavoritosResponse>

}