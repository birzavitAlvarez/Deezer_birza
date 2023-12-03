package com.example.deezer.Musica

data class MusicResponseItem(
    val id_music: Int,
    val titulo_music: String,
    val genero_music: String,
    val artista: String,
    val audio_music: String,
    val img_music: String
)