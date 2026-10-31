// Declara el paquete de la clase
package com.example.aprendeapp

// Importa Intent para navegar entre pantallas
import android.content.Intent
// Importa Bundle para el ciclo de vida
import android.os.Bundle
// Importa la clase base de las pantallas
import androidx.appcompat.app.AppCompatActivity
// Importa el binding generado a partir de activity_main.xml
import com.example.aprendeapp.databinding.ActivityMainBinding
// Importa la pantalla de login
import com.example.aprendeapp.view.LoginActivity
// Importa el manejador de sesión
import com.example.aprendeapp.utils.SesionManager

// Pantalla principal (temporal; en la Parte 5 mostrará la lista de recursos)
class MainActivity : AppCompatActivity() {

    // Acceso a las vistas del layout
    private lateinit var binding: ActivityMainBinding

    // Se ejecuta al crear la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación base
        super.onCreate(savedInstanceState)
        // Crea el manejador de sesión
        val sesion = SesionManager(this)
        // Infla el layout y crea el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Muestra el layout en pantalla
        setContentView(binding.root)
        // Muestra el saludo con el nombre guardado en la sesión
        binding.tvBienvenida.text = getString(R.string.msg_bienvenida_usuario, sesion.obtenerNombre())
        // Elige el texto del rol según si es docente o estudiante
        val rolTexto = if (sesion.esDocente()) R.string.rol_docente else R.string.rol_estudiante
        // Muestra el rol del usuario
        binding.tvRol.text = getString(R.string.main_rol_info, getString(rolTexto))
        // Al pulsar "Cerrar sesión"
        binding.btnLogout.setOnClickListener {
            // Borra la sesión guardada
            sesion.cerrarSesion()
            // Abre el login limpiando el historial de pantallas
            startActivity(Intent(this, LoginActivity::class.java).apply {
                // Evita que el botón "atrás" regrese a la pantalla principal
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // Cierra el apply
            })
            // Cierra el listener
        }
        // Cierra onCreate
    }
// Cierra la clase
}