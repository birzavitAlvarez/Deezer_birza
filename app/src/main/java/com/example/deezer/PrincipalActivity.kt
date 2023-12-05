package com.example.deezer

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deezer.Adapters.MusicAdapter
import com.example.deezer.Login.LoginActivity
//
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.RecyclerView
import com.example.deezer.databinding.ActivityPrincipalBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.deezer.API.RetrofitClient.apiService
import com.example.deezer.Login.UserData
import com.example.deezer.Musica.Result //
import java.util.Locale

// dialog favoritos
import com.example.deezer.databinding.DialogFavoritosBinding
import com.example.deezer.Adapters.FavoritosAdapter
import com.example.deezer.Favoritos.Result as ResultFavoritos //


class PrincipalActivity : AppCompatActivity(),OnQueryTextListener {//

    private var binding: ActivityPrincipalBinding? = null
    private lateinit var adapter: MusicAdapter
    private val datitos = mutableListOf<Result>()

    //
    var id_usuarios: Int? = null
    var nombre:String? = null
    var apellidos:String? = null
    var usuario: String? = null

    // favoritos
    private var bindingDialog: DialogFavoritosBinding? = null
    private lateinit var adapterFavoritosDialog: FavoritosAdapter
    private val datitosFavoritosDialog = mutableListOf<ResultFavoritos>()


    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        //
        val userData = intent.getSerializableExtra("userData") as? UserData
        if (userData != null) {
            id_usuarios = userData.id_usuarios
            nombre = userData.nombre
            apellidos = userData.apellidos
            usuario = userData.usuario
        }

        initRecyclerView(id_usuarios!!)
        listaAlEntrar()

        binding?.tvUserName?.text = nombre

        binding?.svMusic?.setOnQueryTextListener(this)


        binding?.ibSupport?.setOnClickListener {
            val whatsappUrl = "https://wa.link/jhm6ym"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e("WhatsApp", "No se pudo abrir WhatsApp: ${e.localizedMessage}")
                Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.ibLogout?.setOnClickListener {
            logout()
        }

        binding?.ibRefresh?.setOnClickListener {
            listaAlEntrar()
        }

        binding?.ibLibrary?.setOnClickListener {
            favoritosDialog(id_usuarios)
        }
    }

    private fun favoritosDialog(idUsuarios: Int?) {
        val dialog = Dialog(this)
        bindingDialog = DialogFavoritosBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog?.root!!)

        // iniciando el recycler view de productos
        val rvFavoritos: RecyclerView = dialog.findViewById(R.id.rvFavoritos)
        adapterFavoritosDialog = FavoritosAdapter(datitosFavoritosDialog)
        rvFavoritos.layoutManager = LinearLayoutManager(this)
        rvFavoritos.adapter = adapterFavoritosDialog

        //
        listaAlEntrarDialog(rvFavoritos, adapterFavoritosDialog, idUsuarios)
        bindingDialog?.ibCloseFavoritos?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

//    private fun listaAlEntrarDialog(rvFavoritos: RecyclerView, adapterFavoritosDialog: FavoritosAdapter, idUsuarios: Int?) {
//
//    }
    @SuppressLint("NotifyDataSetChanged")
    private fun listaAlEntrarDialog(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>, idUsuarios: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            val request0 = apiService.listFavoritos(idUsuarios!!)
            val response0 = request0.body()
            runOnUiThread {
                if (request0.isSuccessful) {
                    val dataItems = response0?.data?.results ?: emptyList()
                    (adapter as FavoritosAdapter).updateList(dataItems)
                    recyclerView.layoutManager?.scrollToPosition(0)
                } else {
                    showErrorDialog()
                }
            }
        }
    }

    private fun showErrorDialog() {
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
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

    private fun initRecyclerView(id_usuarios: Int) {
        adapter = MusicAdapter(datitos, id_usuarios)
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
