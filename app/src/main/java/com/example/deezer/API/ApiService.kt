package com.example.deezer.API

import com.example.deezer.Favoritos.FavoritosResponse
import com.example.deezer.Favoritos.Respuesta.ResponseFavoritos2
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

    @GET("api/music/listhearth/{id}")
    suspend fun listarMusic(@Path("id") id: Int): Response<MusicResponse>

    // Filtrar por razon_social
    @GET("api/music/busqueda/{id}")
    suspend fun buscarMusic(@Path("id") id: Int, @Query("cancion") cancion: String): Response<MusicResponse>

    // TODO FAVORITOS lista favoritos
    @GET("api/favoritos/listafavoritos/{id}")
    suspend fun listFavoritos(@Path("id") id: Int): Response<FavoritosResponse>


    // id1 = id_music, id2=id_usuarios
    @GET("api/favoritos/insertfavorito/{id1}/{id2}")
    suspend fun insertarFavoritos(@Path("id1") id1: Int, @Path("id2") id2: Int): Response<ResponseFavoritos2>

    // id1 = id_music, id2=id_usuarios
    @GET("api/favoritos/updatefavoritos/{id1}/{id2}")
    suspend fun updateFavoritos(@Path("id1") id1: Int, @Path("id2") id2: Int): Response<ResponseFavoritos2>

}