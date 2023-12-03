package com.example.deezer.Register

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.deezer.Login.LoginActivity
import com.example.deezer.databinding.ActivityRegistroBinding
import com.example.deezer.API.RetrofitClient.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegistroActivity : AppCompatActivity() {

    private var binding: ActivityRegistroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.tvLoginRegister?.setOnClickListener {
            val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // TODO VALIDAR DATA
        binding?.tietNombreRegister?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.tilNombreRegister?.error = if (s?.any { it.isLetterOrDigit() } == true) null else "Nombre requerido"
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding?.tietApellidosRegister?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.tilApellidosRegister?.error = if (s?.any { it.isLetterOrDigit() } == true) null else "Apellidos requeridos"
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding?.tietEmailRegister?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s?.toString()?.trim()
                binding?.tilEmailRegister?.error = if (isEmailValid(email!!)) null else "Email inválido"
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding?.tietPasswordRegister?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.tilPasswordRegister?.error = if (s?.any { it.isLetterOrDigit() } == true) null else "Password requerido"
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        //
        binding?.btnRegister?.setOnClickListener {
            postRegister()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun postRegister() {
        if (binding?.tietNombreRegister?.text.toString().isEmpty()){
            binding?.tilNombreRegister?.error = "Nombre requerido"
            //return@setOnClickListener
            return
        } else if (binding?.tietApellidosRegister?.text.toString().isEmpty()){
            binding?.tilApellidosRegister?.error = "Apellidos requeridos"
            //return@setOnClickListener
            return
        } else if (binding?.tietEmailRegister?.text.toString().isEmpty()){
            binding?.tilEmailRegister?.error = "Email requerido"
            return
        } else if (binding?.tietPasswordRegister?.text.toString().isEmpty()){
            binding?.tilPasswordRegister?.error = "Password requerido"
            return
        }

        val usuario = binding?.tietEmailRegister?.text.toString().toLowerCase()
        val clave = binding?.tietPasswordRegister?.text.toString().toLowerCase()
        val nombre = binding?.tietNombreRegister?.text.toString().toLowerCase()
        val apellidos = binding?.tietApellidosRegister?.text.toString().toLowerCase()

        CoroutineScope(Dispatchers.IO).launch {
            val register_user = RegisterBody(usuario,clave,nombre,apellidos)
            apiService.registerUser(register_user)
            val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            //ShowMessagePositive()
        }
        Toast.makeText(this, "REGISTRO EXITOSO, INICIA SESION", Toast.LENGTH_SHORT).show()


    }


    // Función para validar el correo electrónico
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[A-Za-z](.*)([@]{1})(.{1,})([.]{1})(.{1,})"
        return email.matches(emailPattern.toRegex())
    }

    // Función para validar la contraseña
    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d).{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }
}
