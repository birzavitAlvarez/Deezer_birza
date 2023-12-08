package com.example.deezer.Adapters

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deezer.databinding.SongRowItemBinding
//
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.deezer.Musica.Result
import com.example.deezer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
//
import com.example.deezer.API.RetrofitClient.apiService
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.Channel

class MusicViewHolder(itemView: View, private val id_usuarios: Int) : RecyclerView.ViewHolder(itemView) {

    private val binding: SongRowItemBinding = SongRowItemBinding.bind(itemView)
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler()

    companion object {
        const val REQUEST_WRITE_STORAGE = 123
    }

    fun bind (query: Result,context: Context , id_usuarios: Int){
        //
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        Glide.with(itemView.context).load(query.img_music).into(binding.ivPhotoItemMusic)
        binding.tvNombreMusicaItemMusic.text = query.titulo_music
        binding.tvArtistaItemMusic.text = query.artista

        binding.ibFavorite.setOnClickListener {
            Toast.makeText(context, "id de usuario $id_usuarios", Toast.LENGTH_SHORT).show()
        }

        if (query.liked == 1) {
            binding.ibFavorite.setImageResource(R.drawable.ic_favorite)
        } else if (query.liked == 0) {
            binding.ibFavorite.setImageResource(R.drawable.ic_favorite_border)
        }

        binding.ibFavorite.setOnClickListener {
            updateorinsertxd(query, id_usuarios)
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(query.audio_music)
            prepare()
        }
        binding.seekBarItemMusic.max = mediaPlayer?.duration ?: 0

        handler.post(object : Runnable {
            override fun run() {
                binding.seekBarItemMusic.progress = mediaPlayer?.currentPosition ?: 0
                handler.postDelayed(this, 1000) // Actualizar cada segundo
            }
        })

        val durationInMinutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer?.duration?.toLong() ?: 0)
        val durationInSeconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer?.duration?.toLong() ?: 0) % 60
        val durationText = String.format("%02d:%02d", durationInMinutes, durationInSeconds)
        binding.tvTotalDurationItemMusic.text = durationText

        binding.seekBarItemMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.ibPlayItemMusic.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.ibPlayItemMusic.setImageResource(R.drawable.play_icon)
            } else {
                mediaPlayer?.start()
                binding.ibPlayItemMusic.setImageResource(R.drawable.ic_pause)
                updateSeekBarAndTimeElapsed()
            }
        }


        binding.ibDownload.setOnClickListener {
            if (hasWritePermission(context)) {
                downloadMusic(query.audio_music, context,query.titulo_music)
            } else {
                requestWritePermission(context)
            }
        }


    }

    private fun updateorinsertxd(query: Result, idUsuarios: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.updateFavoritos(query.id_music, idUsuarios)

                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        val message = data.message
                        withContext(Dispatchers.Main) {
                            when {
                                message == 1 && query.liked == 0 -> {
                                    binding.ibFavorite.setImageResource(R.drawable.ic_favorite)
                                    query.liked = 1  // Update the liked state
                                }
                                message == 1 && query.liked == 1 -> {
                                    binding.ibFavorite.setImageResource(R.drawable.ic_favorite_border)
                                    query.liked = 0  // Update the liked state
                                }
                                message == 0 -> {
                                    apiService.insertarFavoritos(query.id_music, idUsuarios)
                                    binding.ibFavorite.setImageResource(R.drawable.ic_favorite)
                                    query.liked = 1  // Update the liked state
                                }
                            }
                        }
                    } else {
                        // El objeto Data es nulo, manejar según sea necesario
                    }
                } else {
                    // La respuesta no fue exitosa, manejar según sea necesario
                }
            } catch (e: Exception) {
                // Manejar excepción
            }
        }
    }



    private fun requestWritePermission(context: Context) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_STORAGE
        )
    }

    private fun hasWritePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun downloadMusic(audioUrl: String, context: Context, nombreCancion:String) {
        val request = DownloadManager.Request(Uri.parse(audioUrl))
            .setTitle("$nombreCancion")
            .setDescription("Descargando canción")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "$nombreCancion.mp3")

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Descargando canción...", Toast.LENGTH_SHORT).show()
    }




    private fun updateSeekBarAndTimeElapsed() {
        handler.post(object : Runnable {
            override fun run() {
                val currentPosition = mediaPlayer?.currentPosition ?: 0

                binding.seekBarItemMusic.progress = currentPosition

                val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition.toLong())
                val elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition.toLong()) % 60

                val elapsedTimeText = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds)
                binding.tvTotalDurationItemMusic.text = elapsedTimeText

                handler.postDelayed(this, 1000)
            }
        })
    }
}