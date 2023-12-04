package com.example.deezer.Adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deezer.databinding.SongRowItemBinding
//
import android.media.MediaPlayer
import android.os.Handler
import android.widget.SeekBar
import com.example.deezer.Musica.Result
import com.example.deezer.R
import java.util.concurrent.TimeUnit

class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding: SongRowItemBinding = SongRowItemBinding.bind(itemView)
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler()

    fun bind (query: Result){
        Glide.with(itemView.context).load(query.img_music).into(binding.ivPhotoItemMusic)
        binding.tvNombreMusicaItemMusic.text = query.titulo_music
        binding.tvArtistaItemMusic.text = query.artista

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