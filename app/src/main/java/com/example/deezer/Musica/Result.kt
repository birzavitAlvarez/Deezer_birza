package com.example.deezer.Musica

data class Result(
    val id_music: Int,
    val artista: String,
    val audio_music: String,
    val genero_music: String,
    val img_music: String,
    val titulo_music: String,
    var liked: Int
)