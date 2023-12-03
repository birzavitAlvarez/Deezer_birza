package com.example.deezer

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deezer.Adapters.MusicAdapter
import com.example.deezer.Login.LoginActivity
//
import com.example.deezer.Musica.MusicResponseItem
import com.example.deezer.databinding.ActivityPrincipalBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.deezer.API.RetrofitClient.apiService


class PrincipalActivity : AppCompatActivity() {

    private var searchRunnable: Runnable? = null
    private lateinit var mediaPlayer: MediaPlayer

    private var binding: ActivityPrincipalBinding? = null

    private lateinit var adapter: MusicAdapter
    private val datitos = mutableListOf<MusicResponseItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        //
        initRecyclerView()
        listaAlEntrar()

        binding?.ibLogout?.setOnClickListener {
            logout()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listaAlEntrar() {
        CoroutineScope(Dispatchers.IO).launch {
            val request0 = apiService.listarMusic()
            val response0 = request0.body()
            runOnUiThread {
                if (request0.isSuccessful) {
                    val dataUsuario = response0 ?: emptyList()
                    datitos.clear()
                    datitos.addAll(dataUsuario)
                    adapter.notifyDataSetChanged()
                } else { showError() }
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerView() {
        adapter = MusicAdapter(datitos)
        binding?.rvMusic?.layoutManager = LinearLayoutManager(this)
        binding?.rvMusic?.adapter = adapter
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
