// Declara el paquete de las vistas
package com.example.aprendeapp.view

// Importa Bundle para el ciclo de vida
import android.os.Bundle
// Importa Patterns para validar el formato del correo
import android.util.Patterns
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
// Importa el binding generado a partir de activity_registro.xml
import com.example.aprendeapp.databinding.ActivityRegistroBinding
// Importa las constantes de rol
import com.example.aprendeapp.utils.Constantes
// Importa el validador de contraseñas
import com.example.aprendeapp.utils.ValidadorPassword
// Importa launch para iniciar corrutinas
import kotlinx.coroutines.launch

// Pantalla de registro de usuario
class RegistroActivity : AppCompatActivity() {

    // Acceso a las vistas del layout
    private lateinit var binding: ActivityRegistroBinding
    // Controlador con la lógica de registro
    private val controlador = AuthController()

    // Se ejecuta al crear la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación base
        super.onCreate(savedInstanceState)
        // Infla el layout y crea el binding
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        // Muestra el layout en pantalla
        setContentView(binding.root)
        // Al pulsar "Registrarme" intenta crear la cuenta
        binding.btnRegistrar.setOnClickListener { intentarRegistro() }
        // Al pulsar el enlace vuelve al login
        binding.btnIrLogin.setOnClickListener { finish() }
        // Cierra onCreate
    }

    // Valida el formulario y registra al usuario en la API
    private fun intentarRegistro() {
        // Lee el nombre sin espacios en los extremos
        val nombre = binding.etNombre.text.toString().trim()
        // Lee el correo sin espacios en los extremos
        val correo = binding.etCorreo.text.toString().trim()
        // Lee la contraseña tal cual
        val password = binding.etPassword.text.toString()
        // Lee la confirmación de contraseña
        val confirmar = binding.etConfirmar.text.toString()
        // Determina el rol elegido en el RadioGroup
        val rol = if (binding.rbDocente.isChecked) Constantes.ROL_DOCENTE else Constantes.ROL_ESTUDIANTE
        // Limpia los errores anteriores de todos los campos
        limpiarErrores()
        // Bandera que indica si el formulario es válido
        var valido = true
        // Valida que el nombre no esté vacío
        if (nombre.isEmpty()) {
            // Muestra el error bajo el campo nombre
            binding.tilNombre.error = getString(R.string.error_campo_vacio)
            // Marca el formulario como inválido
            valido = false
            // Cierra el if
        }
        // Valida que el correo tenga formato correcto
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            // Muestra el error bajo el campo correo
            binding.tilCorreo.error = getString(R.string.error_correo_invalido)
            // Marca el formulario como inválido
            valido = false
            // Cierra el if
        }
        // Obtiene los textos de error de la contraseña según las 5 reglas
        val erroresPassword = ValidadorPassword.validar(password)
        // Si hay reglas incumplidas, las muestra todas juntas
        if (erroresPassword.isNotEmpty()) {
            // Une los mensajes en líneas separadas y los muestra bajo el campo
            binding.tilPassword.error = erroresPassword.joinToString("\n") { getString(it) }
            // Marca el formulario como inválido
            valido = false
            // Cierra el if
        }
        // Valida que ambas contraseñas coincidan
        if (password != confirmar) {
            // Muestra el error bajo el campo de confirmación
            binding.tilConfirmar.error = getString(R.string.error_passwords_no_coinciden)
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
            // Evalúa el resultado del registro
            when (val resultado = controlador.registrar(nombre, correo, password, rol)) {
                // Si el registro fue correcto
                is ResultadoAuth.Exito -> {
                    // Informa que la cuenta fue creada
                    Toast.makeText(this@RegistroActivity, getString(R.string.msg_registro_exitoso), Toast.LENGTH_LONG).show()
                    // Cierra esta pantalla para volver al login
                    finish()
                    // Cierra el caso de éxito
                }
                // Si el registro falló
                is ResultadoAuth.Error -> {
                    // Oculta el loader y habilita el botón
                    mostrarCarga(false)
                    // Muestra el motivo del error
                    Toast.makeText(this@RegistroActivity, getString(resultado.mensajeId), Toast.LENGTH_LONG).show()
                    // Cierra el caso de error
                }
                // Cierra el when
            }
            // Cierra la corrutina
        }
        // Cierra intentarRegistro
    }

    // Quita los mensajes de error de todos los campos
    private fun limpiarErrores() {
        // Limpia el error del nombre
        binding.tilNombre.error = null
        // Limpia el error del correo
        binding.tilCorreo.error = null
        // Limpia el error de la contraseña
        binding.tilPassword.error = null
        // Limpia el error de la confirmación
        binding.tilConfirmar.error = null
        // Cierra la función
    }

    // Muestra u oculta el loader y bloquea el botón mientras carga
    private fun mostrarCarga(cargando: Boolean) {
        // Cambia la visibilidad del loader según el estado
        binding.pbCargando.visibility = if (cargando) View.VISIBLE else View.GONE
        // Deshabilita el botón mientras se consulta la API
        binding.btnRegistrar.isEnabled = !cargando
        // Cierra la función
    }
// Cierra la clase
}