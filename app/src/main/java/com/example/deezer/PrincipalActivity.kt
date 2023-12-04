package com.example.deezer

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deezer.Adapters.MusicAdapter
import com.example.deezer.Login.LoginActivity
//
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import com.example.deezer.databinding.ActivityPrincipalBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.deezer.API.RetrofitClient.apiService
import com.example.deezer.Login.UserData
import com.example.deezer.Musica.Result //
import java.util.Locale


class PrincipalActivity : AppCompatActivity(),OnQueryTextListener {//

    private var searchRunnable: Runnable? = null
    private lateinit var mediaPlayer: MediaPlayer

    private var binding: ActivityPrincipalBinding? = null

    private lateinit var adapter: MusicAdapter
    private val datitos = mutableListOf<Result>()

    //
    var id_usuarios: Int? = null
    var nombre:String? = null
    var apellidos:String? = null
    var usuario: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        //
        initRecyclerView()
        listaAlEntrar()
        val userData = intent.getSerializableExtra("userData") as? UserData
        if (userData != null) {
            id_usuarios = userData.id_usuarios
            nombre = userData.nombre
            apellidos = userData.apellidos
            usuario = userData.usuario
        }

        binding?.tvUserName?.text = nombre

        binding?.svMusic?.setOnQueryTextListener(this)


        binding?.ibSupport?.setOnClickListener {

        }

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
                    val dataMusic = response0?.data?.results ?: emptyList<Result>()
                    datitos.clear()
                    datitos.addAll(dataMusic)
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()){
            searchByItem(query.lowercase(Locale.ROOT))
        }
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchByItem(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val request0 = apiService.buscarMusic(query)
            val response0 = request0.body()
            runOnUiThread {
                if (request0.isSuccessful) {
                    val dataMusic = response0?.data?.results ?: emptyList<Result>()
                    datitos.clear()
                    datitos.addAll(dataMusic)
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }
                hideKeyboard()
            }
        }
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun performSearch(query: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.buscarMusic(query ?: "").body()
            runOnUiThread {
                if (response != null) {
                    adapter.notifyDataSetChanged()
                } else { showError() }
            }
        }
    }
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding?.ActivityPrincipalPadre?.windowToken,0)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}
