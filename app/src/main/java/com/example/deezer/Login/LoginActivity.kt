package com.example.deezer.Login


import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deezer.R
import com.example.deezer.Register.RegistroActivity
import com.example.deezer.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.deezer.API.RetrofitClient.apiService
import com.example.deezer.PrincipalActivity
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // TODO 1 PARA SESION GRABADA
        checkSession()

        binding?.tietEmailLogin?.requestFocus()


        // TODO VALIDAR
        binding?.tietEmailLogin?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s?.toString()?.trim()
                binding?.tilEmailLogin?.error = if (isEmailValid(email!!)) null else "Email inválido"
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding?.tietPasswordLogin?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.tilPasswordLogin?.error = if (s?.any { it.isLetterOrDigit() } == true) null else "Password requerido"
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        // TODO FIN VALIDAR
        // TODO LOGIN
        binding?.btnLogin?.setOnClickListener {
            val username = binding?.tietEmailLogin?.text.toString().toLowerCase()
            val password = binding?.tietPasswordLogin?.text.toString().toLowerCase()

            if (username.isEmpty() && password.isEmpty()){
                binding?.tilEmailLogin?.error = "Email requerido"
                binding?.tilPasswordLogin?.error = "Password requerido"
                return@setOnClickListener
            } else if (username.isEmpty()) {
                binding?.tilEmailLogin?.error = "Usuario requerido"
                return@setOnClickListener
            } else if (password.isEmpty()){
                binding?.tilPasswordLogin?.error = "Password requerido"
                return@setOnClickListener
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    loginUser(username, password)
                }
            }
        }
        // TODO FIN LOGIN


        binding?.btnRegister?.setOnClickListener {
            val intent = Intent(applicationContext, RegistroActivity::class.java)
            startActivity(intent)
        }

        binding?.ibFacebook?.setOnClickListener {
            openSocialMedia("https://www.facebook.com/deezer")
        }

        binding?.ibTwitter?.setOnClickListener {
            openSocialMedia("https://twitter.com/deezer")
        }

        binding?.ibInstagram?.setOnClickListener {
            openSocialMedia("https://www.instagram.com/deezer/")
        }
    }

    suspend fun loginUser(username: String, password: String) {
        val (status, statusMessage) = checkUserAndPassword(username, password)
        runOnUiThread {
            when (status) {
                200 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = apiService.login(LoginRequest(username, password))
                            withContext(Dispatchers.Main) {
                                handleLoginResponse(response)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@LoginActivity, "Error en la solicitud de red", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                404 -> {
                    showErrorDialogLogin()
                }

                else -> {
                    showErrorDialogLogin()
                }
            }
        }
    }

    private fun handleLoginResponse(response: Response<LoginResponse>) {
        if (response.isSuccessful) {
            val loginResponse = response.body()

            if (loginResponse != null && loginResponse.data.isNotEmpty()) {
                val datos = loginResponse.data[0]
                val id_usuarios = datos.id_usuarios
                val nombre = datos.nombre
                val apellidos = datos.apellidos
                val usuario = datos.usuario

                val userData = UserData(id_usuarios, nombre, apellidos, usuario)
                val intent = Intent(this@LoginActivity, PrincipalActivity::class.java)

                // TODO 2 PARA GRABAR SESION
                saveUserDataInPreferences(userData)
                //
                intent.putExtra("userData", userData)
                startActivity(intent)
            } else {
                //
            }
        } else {
            //
        }
    }

    private suspend fun checkUserAndPassword(username: String, password: String): Pair<Int, String?> {
        val request = LoginRequest(username, password)
        try {
            val response = apiService.login(request)
            if (response.isSuccessful) {
                val loginResponse = response.body()

                if (loginResponse != null) {
                    return Pair(loginResponse.status, loginResponse.statusMessage)
                }
            }
        } catch (e: Exception) {
            //
        }
        return Pair(500, "Error en la solicitud")
    }
    //

    private fun showErrorDialogLogin() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_error)
        val btndialogLoginBadRequestOk: Button = dialog.findViewById(R.id.btndialogLoginBadRequestOk)
        btndialogLoginBadRequestOk.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun openSocialMedia(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[A-Za-z](.*)([@]{1})(.{1,})([.]{1})(.{1,})"
        return email.matches(emailPattern.toRegex())
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d).{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    // TODO 3 PARA GRABAR SESION
    // Después de un inicio de sesión exitoso
    private fun saveUserDataInPreferences(userData: UserData) {
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt("id_usuarios", userData.id_usuarios)
        editor.putString("nombre", userData.nombre)
        editor.putString("apellidos", userData.apellidos)
        editor.putString("usuario", userData.usuario)

        editor.apply()
    }

    // TODO 4 PARA GRABAR SESION
    private fun checkSession() {
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id_usuarios", -1)

        if (userId != -1) {
            // El usuario ha iniciado sesión previamente, dirigirlo a la pantalla principal
            val userData = UserData(
                userId,
                sharedPreferences.getString("nombre", "") ?: "",
                sharedPreferences.getString("apellidos", "") ?: "",
                sharedPreferences.getString("usuario", "") ?: ""
            )

            val intent = Intent(this, PrincipalActivity::class.java)
            intent.putExtra("userData", userData)
            startActivity(intent)
            finish() // Para cerrar la actividad actual
        }
    }
    //

}
