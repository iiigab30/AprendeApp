// Declara el paquete de las vistas
package com.example.aprendeapp.view

// Importa Bundle para el ciclo de vida
import android.os.Bundle
// Importa View para controlar el loader
import android.view.View
// Importa ArrayAdapter para llenar el spinner
import android.widget.ArrayAdapter
// Importa Toast para mensajes cortos
import android.widget.Toast
// Importa la clase base de las pantallas
import androidx.appcompat.app.AppCompatActivity
// Importa lifecycleScope para lanzar corrutinas
import androidx.lifecycle.lifecycleScope
// Importa la clase R de la app
import com.example.aprendeapp.R
// Importa el controlador y el resultado de operaciones
import com.example.aprendeapp.controller.RecursoController
import com.example.aprendeapp.controller.ResultadoOperacion
// Importa el binding generado a partir de activity_formulario_recurso.xml
import com.example.aprendeapp.databinding.ActivityFormularioRecursoBinding
// Importa el modelo de recurso
import com.example.aprendeapp.model.Recurso
// Importa las utilidades
import com.example.aprendeapp.utils.SesionManager
import com.example.aprendeapp.utils.TiposRecurso
// Importa launch para iniciar corrutinas
import kotlinx.coroutines.launch

// Pantalla del docente para crear o editar un recurso
class FormularioRecursoActivity : AppCompatActivity() {

    // Objeto con la clave usada para enviar el recurso entre pantallas
    companion object {
        // Clave del extra que contiene el recurso a editar
        const val EXTRA_RECURSO = "extra_recurso"
        // Cierra el companion
    }

    // Acceso a las vistas del layout
    private lateinit var binding: ActivityFormularioRecursoBinding
    // Controlador con la lógica de recursos
    private val controlador = RecursoController()
    // Recurso que se está editando (null cuando se está creando uno nuevo)
    private var recursoEditando: Recurso? = null

    // Se ejecuta al crear la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación base
        super.onCreate(savedInstanceState)
        // Seguridad: si el usuario no es docente, cierra la pantalla
        if (!SesionManager(this).esDocente()) {
            // Cierra la pantalla
            finish()
            // Detiene la ejecución del resto del método
            return
            // Cierra el if
        }
        // Infla el layout y crea el binding
        binding = ActivityFormularioRecursoBinding.inflate(layoutInflater)
        // Muestra el layout en pantalla
        setContentView(binding.root)
        // Lee el recurso enviado desde la lista (si es edición)
        @Suppress("DEPRECATION")
        recursoEditando = intent.getSerializableExtra(EXTRA_RECURSO) as? Recurso
        // Llena el spinner con los tipos de recurso
        configurarSpinner()
        // La flecha de la barra cierra la pantalla
        binding.toolbar.setNavigationOnClickListener { finish() }
        // Si hay un recurso para editar, prellena el formulario
        recursoEditando?.let { precargarFormulario(it) }
        // Al pulsar "Guardar" valida y envía los datos
        binding.btnGuardar.setOnClickListener { guardar() }
        // Al pulsar "Cancelar" cierra la pantalla
        binding.btnCancelar.setOnClickListener { finish() }
        // Cierra onCreate
    }

    // Llena el spinner con los textos visibles de cada tipo
    private fun configurarSpinner() {
        // Convierte cada tipo de la API en su texto visible
        val etiquetas = TiposRecurso.TODOS.map { getString(TiposRecurso.etiqueta(it)) }
        // Crea el adaptador del spinner
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetas)
        // Define el diseño de la lista desplegable
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Asigna el adaptador al spinner
        binding.spTipo.adapter = adaptador
        // Cierra la función
    }

    // Rellena el formulario con los datos del recurso a editar
    private fun precargarFormulario(recurso: Recurso) {
        // Cambia el título de la barra a "Editar recurso"
        binding.toolbar.title = getString(R.string.form_titulo_editar)
        // Muestra el título actual
        binding.etTitulo.setText(recurso.titulo)
        // Muestra la descripción actual
        binding.etDescripcion.setText(recurso.descripcion)
        // Muestra el enlace actual
        binding.etEnlace.setText(recurso.enlace)
        // Muestra la URL de imagen actual
        binding.etImagen.setText(recurso.imagen)
        // Selecciona el tipo actual en el spinner (si no lo encuentra, usa el primero)
        binding.spTipo.setSelection(TiposRecurso.TODOS.indexOf(recurso.tipo.lowercase()).coerceAtLeast(0))
        // Cierra la función
    }

    // Valida el formulario y guarda el recurso en la API
    private fun guardar() {
        // Lee el título sin espacios en los extremos
        val titulo = binding.etTitulo.text.toString().trim()
        // Lee la descripción sin espacios en los extremos
        val descripcion = binding.etDescripcion.text.toString().trim()
        // Lee el enlace sin espacios en los extremos
        val enlace = binding.etEnlace.text.toString().trim()
        // Lee la URL de la imagen sin espacios en los extremos
        val imagen = binding.etImagen.text.toString().trim()
        // Obtiene el tipo elegido según la posición del spinner
        val tipo = TiposRecurso.TODOS[binding.spTipo.selectedItemPosition]
        // Limpia los errores anteriores
        limpiarErrores()
        // Bandera que indica si el formulario es válido
        var valido = true
        // Valida la longitud del título
        if (titulo.length < 3) {
            // Muestra el error bajo el campo
            binding.tilTitulo.error = getString(R.string.error_titulo_corto)
            // Marca el formulario como inválido
            valido = false
            // Cierra el if
        }
        // Valida la longitud de la descripción
        if (descripcion.length < 10) {
            // Muestra el error bajo el campo
            binding.tilDescripcion.error = getString(R.string.error_descripcion_corta)
            // Marca el formulario como inválido
            valido = false
            // Cierra el if
        }
        // Valida el formato del enlace
        if (!controlador.esUrlValida(enlace)) {
            // Muestra el error bajo el campo
            binding.tilEnlace.error = getString(R.string.error_url_invalida)
            // Marca el formulario como inválido
            valido = false
            // Cierra el if
        }
        // Valida el formato de la URL de la imagen
        if (!controlador.esUrlValida(imagen)) {
            // Muestra el error bajo el campo
            binding.tilImagen.error = getString(R.string.error_url_invalida)
            // Marca el formulario como inválido
            valido = false
            // Cierra el if
        }
        // Si hay errores, no consulta la API
        if (!valido) return
        // Toma el recurso original (si se está editando)
        val original = recursoEditando
        // Si edita, copia el original cambiando solo los campos del formulario (así conserva las calificaciones)
        val recurso = original?.copy(titulo = titulo, descripcion = descripcion, tipo = tipo, enlace = enlace, imagen = imagen)
        // Si crea, arma un recurso nuevo
            ?: Recurso(titulo = titulo, descripcion = descripcion, tipo = tipo, enlace = enlace, imagen = imagen)
        // Muestra el loader y bloquea el botón
        mostrarCarga(true)
        // Lanza una corrutina para consultar la API
        lifecycleScope.launch {
            // Si es edición hace PUT; si es creación hace POST
            val resultado = if (original == null) controlador.crear(recurso) else controlador.actualizar(recurso)
            // Evalúa el resultado
            when (resultado) {
                // Si todo salió bien
                is ResultadoOperacion.Exito -> {
                    // Elige el mensaje según si creó o actualizó
                    val mensaje = if (original == null) R.string.msg_recurso_creado else R.string.msg_recurso_actualizado
                    // Muestra el toast de confirmación
                    Toast.makeText(this@FormularioRecursoActivity, getString(mensaje), Toast.LENGTH_SHORT).show()
                    // Cierra la pantalla y vuelve a la lista
                    finish()
                    // Cierra el caso de éxito
                }
                // Si hubo un error
                is ResultadoOperacion.Error -> {
                    // Oculta el loader y habilita el botón
                    mostrarCarga(false)
                    // Muestra el motivo del error
                    Toast.makeText(this@FormularioRecursoActivity, getString(resultado.mensajeId), Toast.LENGTH_LONG).show()
                    // Cierra el caso de error
                }
                // Cierra el when
            }
            // Cierra la corrutina
        }
        // Cierra la función
    }

    // Quita los mensajes de error de todos los campos
    private fun limpiarErrores() {
        // Limpia el error del título
        binding.tilTitulo.error = null
        // Limpia el error de la descripción
        binding.tilDescripcion.error = null
        // Limpia el error del enlace
        binding.tilEnlace.error = null
        // Limpia el error de la imagen
        binding.tilImagen.error = null
        // Cierra la función
    }

    // Muestra u oculta el loader y bloquea el botón mientras guarda
    private fun mostrarCarga(cargando: Boolean) {
        // Cambia la visibilidad del loader según el estado
        binding.pbCargando.visibility = if (cargando) View.VISIBLE else View.GONE
        // Deshabilita el botón de guardar mientras se consulta la API
        binding.btnGuardar.isEnabled = !cargando
        // Cierra la función
    }
// Cierra la clase
}