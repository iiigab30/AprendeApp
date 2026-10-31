// Declara el paquete de las vistas
package com.example.aprendeapp.view

// Importa Intent para navegar entre pantallas
import android.content.Intent
// Importa Bundle para el ciclo de vida
import android.os.Bundle
// Importa View para controlar la visibilidad del loader
import android.view.View
// Importa Toast para mensajes cortos
import android.widget.Toast
// Importa la clase base de las pantallas
import androidx.appcompat.app.AppCompatActivity
// Importa lifecycleScope para lanzar corrutinas
import androidx.lifecycle.lifecycleScope
// Importa la clase R de la app
import com.example.aprendeapp.R
// Importa el controlador de autenticación
import com.example.aprendeapp.controller.AuthController
// Importa el resultado de autenticación
import com.example.aprendeapp.controller.ResultadoAuth
// Importa la pantalla principal
import com.example.aprendeapp.MainActivity
// Importa el binding generado a partir de activity_login.xml
import com.example.aprendeapp.databinding.ActivityLoginBinding
// Importa el manejador de sesión
import com.example.aprendeapp.utils.SesionManager
// Importa launch para iniciar corrutinas
import kotlinx.coroutines.launch

// Pantalla de inicio de sesión
class LoginActivity : AppCompatActivity() {

    // Acceso a las vistas del layout
    private lateinit var binding: ActivityLoginBinding
    // Controlador con la lógica de login
    private val controlador = AuthController()
    // Manejador de la sesión local
    private lateinit var sesion: SesionManager

    // Se ejecuta al crear la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación base
        super.onCreate(savedInstanceState)
        // Crea el manejador de sesión
        sesion = SesionManager(this)
        // Si ya hay sesión guardada, salta directo a la pantalla principal
        if (sesion.estaLogueado()) {
            // Abre la pantalla principal y cierra el login
            irAPrincipal()
            // Detiene la ejecución del resto del método
            return
            // Cierra el if
        }
        // Infla el layout y crea el binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        // Muestra el layout en pantalla
        setContentView(binding.root)
        // Al pulsar "Ingresar" intenta iniciar sesión
        binding.btnLogin.setOnClickListener { intentarLogin() }
        // Al pulsar el enlace abre la pantalla de registro
        binding.btnIrRegistro.setOnClickListener {
            // Navega hacia RegistroActivity
            startActivity(Intent(this, RegistroActivity::class.java))
            // Cierra el listener
        }
        // Cierra onCreate
    }

    // Valida los campos y consulta la API
    private fun intentarLogin() {
        // Lee el correo sin espacios en los extremos
        val correo = binding.etCorreo.text.toString().trim()
        // Lee la contraseña tal cual (los espacios podrían ser parte de ella)
        val password = binding.etPassword.text.toString()
        // Limpia errores anteriores del correo
        binding.tilCorreo.error = null
        // Limpia errores anteriores de la contraseña
        binding.tilPassword.error = null
        // Bandera que indica si el formulario es válido
        var valido = true
        // Si el correo está vacío marca el error
        if (correo.isEmpty()) {
            // Muestra el error bajo el campo
            binding.tilCorreo.error = getString(R.string.error_campo_vacio)
            // Marca el formulario como inválido
            valido = false
            // Cierra el if
        }
        // Si la contraseña está vacía marca el error
        if (password.isEmpty()) {
            // Muestra el error bajo el campo
            binding.tilPassword.error = getString(R.string.error_campo_vacio)
            // Marca el formulario como inválido
            valido = false
            // Cierra el if
        }
        // Si hay errores, no consulta la API
        if (!valido) return
        // Muestra el loader y bloquea el botón
        mostrarCarga(true)
        // Lanza una corrutina para consultar la API
        lifecycleScope.launch {
            // Evalúa el resultado del login
            when (val resultado = controlador.login(correo, password)) {
                // Si el login fue correcto
                is ResultadoAuth.Exito -> {
                    // Guarda la sesión del usuario
                    sesion.guardarSesion(resultado.usuario)
                    // Muestra un toast de bienvenida
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.msg_bienvenida_usuario, resultado.usuario.nombre),
                        Toast.LENGTH_SHORT
                    ).show()
                    // Abre la pantalla principal
                    irAPrincipal()
                    // Cierra el caso de éxito
                }
                // Si el login falló
                is ResultadoAuth.Error -> {
                    // Oculta el loader y habilita el botón
                    mostrarCarga(false)
                    // Muestra el motivo del error
                    Toast.makeText(this@LoginActivity, getString(resultado.mensajeId), Toast.LENGTH_LONG).show()
                    // Cierra el caso de error
                }
                // Cierra el when
            }
            // Cierra la corrutina
        }
        // Cierra intentarLogin
    }

    // Muestra u oculta el loader y bloquea el botón mientras carga
    private fun mostrarCarga(cargando: Boolean) {
        // Cambia la visibilidad del loader según el estado
        binding.pbCargando.visibility = if (cargando) View.VISIBLE else View.GONE
        // Deshabilita el botón mientras se consulta la API
        binding.btnLogin.isEnabled = !cargando
        // Cierra la función
    }

    // Abre la pantalla principal y cierra el login para que "atrás" no regrese aquí
    private fun irAPrincipal() {
        // Navega hacia MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        // Cierra esta pantalla
        finish()
        // Cierra la función
    }
// Cierra la clase
}