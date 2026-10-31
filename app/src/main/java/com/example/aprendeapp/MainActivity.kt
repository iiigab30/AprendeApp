// Declara el paquete de la clase
package com.example.aprendeapp

// Importa ActivityNotFoundException por si no hay app para abrir el enlace
import android.content.ActivityNotFoundException
// Importa Intent para navegar y abrir enlaces
import android.content.Intent
// Importa Uri para convertir el texto del enlace en una dirección
import android.net.Uri
// Importa Bundle para el ciclo de vida
import android.os.Bundle
// Importa View para controlar visibilidades
import android.view.View
// Importa AdapterView para escuchar la selección de los spinners
import android.widget.AdapterView
// Importa ArrayAdapter para llenar los spinners
import android.widget.ArrayAdapter
// Importa Spinner
import android.widget.Spinner
// Importa Toast para mensajes cortos
import android.widget.Toast
// Importa la clase base de las pantallas
import androidx.appcompat.app.AppCompatActivity
// Importa la función que reacciona cuando cambia el texto de un campo
import androidx.core.widget.doAfterTextChanged
// Importa lifecycleScope para lanzar corrutinas
import androidx.lifecycle.lifecycleScope
// Importa el administrador de diseño que coloca las tarjetas en una columna
import androidx.recyclerview.widget.LinearLayoutManager
// Importa el controlador y sus tipos de apoyo
import com.example.aprendeapp.controller.CampoBusqueda
import com.example.aprendeapp.controller.Orden
import com.example.aprendeapp.controller.RecursoController
import com.example.aprendeapp.controller.ResultadoRecursos
// Importa el binding generado a partir de activity_main.xml
import com.example.aprendeapp.databinding.ActivityMainBinding
// Importa el modelo de recurso
import com.example.aprendeapp.model.Recurso
// Importa las utilidades
import com.example.aprendeapp.utils.SesionManager
import com.example.aprendeapp.utils.TiposRecurso
// Importa el adaptador y el login
import com.example.aprendeapp.view.LoginActivity
import com.example.aprendeapp.view.RecursoAdapter
// Importa launch para iniciar corrutinas
import kotlinx.coroutines.launch
// Importa el constructor de diálogos de Material
import com.google.android.material.dialog.MaterialAlertDialogBuilder
// Importa el resultado de operaciones del controlador
import com.example.aprendeapp.controller.ResultadoOperacion
// Importa la pantalla del formulario
import com.example.aprendeapp.view.FormularioRecursoActivity

// Pantalla principal: lista de recursos con búsqueda, filtro y orden
class MainActivity : AppCompatActivity() {

    // Acceso a las vistas del layout
    private lateinit var binding: ActivityMainBinding
    // Manejador de la sesión local
    private lateinit var sesion: SesionManager
    // Adaptador que dibuja las tarjetas
    private lateinit var adaptador: RecursoAdapter
    // Controlador con la lógica de recursos
    private val controlador = RecursoController()

    // Lista completa de recursos recibida de la API
    private var todos: List<Recurso> = emptyList()
    // Campo de búsqueda elegido en el spinner
    private var campoBusqueda = CampoBusqueda.TODO
    // Criterio de orden elegido en el spinner
    private var orden = Orden.TITULO_ASC
    // Tipo elegido en los chips (null significa todos)
    private var tipoFiltro: String? = null
    // Indica si se está cargando desde la API
    private var cargando = false

    // Se ejecuta al crear la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación base
        super.onCreate(savedInstanceState)
        // Crea el manejador de sesión
        sesion = SesionManager(this)
        // Infla el layout y crea el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Muestra el layout en pantalla
        setContentView(binding.root)
        // Configura la barra superior
        configurarToolbar()
        // Configura la lista de tarjetas
        configurarLista()
        // Configura búsqueda, spinners y chips
        configurarFiltros()
        // Cierra onCreate
        // Muestra el botón de agregar solo si el usuario es docente
        configurarBotonAgregar()
    }

    // Se ejecuta cada vez que la pantalla vuelve a primer plano (así la lista siempre está actualizada)
    override fun onResume() {
        // Llama a la implementación base
        super.onResume()
        // Recarga los recursos desde la API
        cargarRecursos()
        // Cierra onResume
    }

    // Configura la barra superior con el menú y el rol del usuario
    private fun configurarToolbar() {
        // Elige el texto del rol según si es docente o estudiante
        val rolTexto = if (sesion.esDocente()) R.string.rol_docente else R.string.rol_estudiante
        // Muestra el rol como subtítulo de la barra
        binding.toolbar.subtitle = getString(R.string.main_rol_info, getString(rolTexto))
        // Carga el menú con la opción de cerrar sesión
        binding.toolbar.inflateMenu(R.menu.menu_main)
        // Escucha los clics en las opciones del menú
        binding.toolbar.setOnMenuItemClickListener { opcion ->
            // Si se pulsó "Cerrar sesión"
            if (opcion.itemId == R.id.action_logout) {
                // Cierra la sesión y vuelve al login
                cerrarSesion()
                // Indica que el clic fue atendido
                true
                // Si fue otra opción
            } else {
                // Indica que no se atendió
                false
                // Cierra el if
            }
            // Cierra el listener
        }
        // Cierra la función
    }

    // Configura el RecyclerView y su adaptador
    private fun configurarLista() {
        // Crea el adaptador con las acciones de cada tarjeta
        adaptador = RecursoAdapter(
            // Le indica si debe mostrar los botones de docente
            esDocente = sesion.esDocente(),
            // Al tocar un enlace se abre en el navegador
            alAbrirEnlace = { recurso -> abrirEnlace(recurso.enlace) },
            // Al pulsar "Editar" abre el formulario con el recurso
            alEditar = { recurso -> abrirFormulario(recurso) },
            // Al pulsar "Eliminar" pide confirmación
            alEliminar = { recurso -> confirmarEliminar(recurso) }
            // Cierra los parámetros
        )
        // Coloca las tarjetas en una sola columna vertical
        binding.rvRecursos.layoutManager = LinearLayoutManager(this)
        // Conecta el adaptador con la lista
        binding.rvRecursos.adapter = adaptador
        // Cierra la función
    }

    // Configura el buscador, los spinners y los chips
    private fun configurarFiltros() {
        // Configura el spinner de campo de búsqueda
        configurarSpinner(binding.spBuscarPor, R.array.campos_busqueda) { posicion ->
            // Guarda el campo elegido según la posición
            campoBusqueda = CampoBusqueda.values()[posicion]
            // Vuelve a aplicar los filtros
            aplicarFiltros()
            // Cierra el spinner de búsqueda
        }
        // Configura el spinner de orden
        configurarSpinner(binding.spOrden, R.array.opciones_orden) { posicion ->
            // Guarda el orden elegido según la posición
            orden = Orden.values()[posicion]
            // Vuelve a aplicar los filtros
            aplicarFiltros()
            // Cierra el spinner de orden
        }
        // Cada vez que cambia el texto del buscador, vuelve a filtrar
        binding.etBuscar.doAfterTextChanged { aplicarFiltros() }
        // Cada vez que cambia el chip seleccionado
        binding.chipGroupTipo.setOnCheckedStateChangeListener { _, idsSeleccionados ->
            // Traduce el chip seleccionado al valor de tipo que usa la API
            tipoFiltro = when (idsSeleccionados.firstOrNull()) {
                // Chip de libros
                R.id.chipLibro -> TiposRecurso.LIBRO
                // Chip de videos
                R.id.chipVideo -> TiposRecurso.VIDEO
                // Chip de artículos
                R.id.chipArticulo -> TiposRecurso.ARTICULO
                // Chip de tutoriales
                R.id.chipTutorial -> TiposRecurso.TUTORIAL
                // Chip "Todos": sin filtro de tipo
                else -> null
                // Cierra el when
            }
            // Vuelve a aplicar los filtros
            aplicarFiltros()
            // Cierra el listener de chips
        }
        // Cierra la función
    }

    // Llena un spinner con un arreglo de strings.xml y avisa la posición elegida
    private fun configurarSpinner(spinner: Spinner, arregloId: Int, alSeleccionar: (Int) -> Unit) {
        // Crea el adaptador a partir del arreglo de textos
        val adaptadorSpinner = ArrayAdapter.createFromResource(this, arregloId, android.R.layout.simple_spinner_item)
        // Define el diseño de la lista desplegable
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Asigna el adaptador al spinner
        spinner.adapter = adaptadorSpinner
        // Escucha la selección del usuario
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Se ejecuta al elegir una opción
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Avisa la posición elegida
                alSeleccionar(position)
                // Cierra la función
            }
            // Se ejecuta si no hay nada seleccionado (no hacemos nada)
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            // Cierra el listener
        }
        // Cierra la función
    }

    // Pide los recursos a la API y actualiza la pantalla
    private fun cargarRecursos() {
        // Muestra el loader
        mostrarCarga(true)
        // Lanza una corrutina para no bloquear la pantalla
        lifecycleScope.launch {
            // Evalúa el resultado de la carga
            when (val resultado = controlador.obtenerRecursos()) {
                // Si la carga fue correcta
                is ResultadoRecursos.Exito -> {
                    // Guarda la lista completa
                    todos = resultado.recursos
                    // Oculta el loader
                    mostrarCarga(false)
                    // Muestra la lista aplicando búsqueda, filtro y orden
                    aplicarFiltros()
                    // Cierra el caso de éxito
                }
                // Si la carga falló
                is ResultadoRecursos.Error -> {
                    // Oculta el loader
                    mostrarCarga(false)
                    // Muestra el motivo del error
                    Toast.makeText(this@MainActivity, getString(resultado.mensajeId), Toast.LENGTH_LONG).show()
                    // Cierra el caso de error
                }
                // Cierra el when
            }
            // Cierra la corrutina
        }
        // Cierra la función
    }

    // Aplica búsqueda, filtro y orden, y actualiza la lista en pantalla
    private fun aplicarFiltros() {
        // Pide al controlador la lista ya filtrada y ordenada
        val resultado = controlador.filtrarYOrdenar(
            // Lista completa
            todos,
            // Texto actual del buscador
            binding.etBuscar.text.toString(),
            // Campo de búsqueda elegido
            campoBusqueda,
            // Tipo elegido en los chips
            tipoFiltro,
            // Orden elegido
            orden
            // Cierra los parámetros
        )
        // Entrega la nueva lista al adaptador
        adaptador.submitList(resultado)
        // Muestra el mensaje vacío solo si terminó de cargar y no hay resultados
        binding.tvVacio.visibility = if (!cargando && resultado.isEmpty()) View.VISIBLE else View.GONE
        // Cierra la función
    }

    // Muestra u oculta el loader
    private fun mostrarCarga(estado: Boolean) {
        // Guarda el estado de carga
        cargando = estado
        // Cambia la visibilidad del loader
        binding.pbCargando.visibility = if (estado) View.VISIBLE else View.GONE
        // Mientras carga, oculta el mensaje de lista vacía
        if (estado) binding.tvVacio.visibility = View.GONE
        // Cierra la función
    }

    // Muestra el botón flotante solo a docentes y le asigna su acción
    private fun configurarBotonAgregar() {
        // Si el usuario es docente muestra el botón; si no, lo mantiene oculto
        binding.fabAgregar.visibility = if (sesion.esDocente()) View.VISIBLE else View.GONE
        // Al pulsarlo abre el formulario vacío para crear un recurso
        binding.fabAgregar.setOnClickListener { abrirFormulario(null) }
        // Cierra la función
    }

    // Abre el formulario: con un recurso para editar, o con null para crear
    private fun abrirFormulario(recurso: Recurso?) {
        // Crea el intent hacia la pantalla del formulario
        val intent = Intent(this, FormularioRecursoActivity::class.java)
        // Si hay recurso, lo envía para prellenar el formulario
        if (recurso != null) intent.putExtra(FormularioRecursoActivity.EXTRA_RECURSO, recurso)
        // Abre la pantalla
        startActivity(intent)
        // Cierra la función
    }

    // Pide confirmación antes de eliminar un recurso
    private fun confirmarEliminar(recurso: Recurso) {
        // Construye y muestra el diálogo de confirmación
        MaterialAlertDialogBuilder(this)
            // Título del diálogo
            .setTitle(R.string.dialogo_eliminar_titulo)
            // Mensaje con el nombre del recurso
            .setMessage(getString(R.string.dialogo_eliminar_mensaje, recurso.titulo))
            // Botón de cancelar: no hace nada y cierra el diálogo
            .setNegativeButton(R.string.btn_cancelar, null)
            // Botón de confirmar: elimina el recurso
            .setPositiveButton(R.string.dialogo_eliminar_confirmar) { _, _ -> eliminarRecurso(recurso) }
            // Muestra el diálogo
            .show()
        // Cierra la función
    }

    // Elimina un recurso en la API y recarga la lista
    private fun eliminarRecurso(recurso: Recurso) {
        // Toma el id del recurso; si no tiene, no hace nada
        val id = recurso.id ?: return
        // Muestra el loader
        mostrarCarga(true)
        // Lanza una corrutina para consultar la API
        lifecycleScope.launch {
            // Evalúa el resultado de la eliminación
            when (val resultado = controlador.eliminar(id)) {
                // Si se eliminó correctamente
                is ResultadoOperacion.Exito -> {
                    // Informa al usuario
                    Toast.makeText(this@MainActivity, getString(R.string.msg_recurso_eliminado), Toast.LENGTH_SHORT).show()
                    // Recarga la lista para que desaparezca la tarjeta
                    cargarRecursos()
                    // Cierra el caso de éxito
                }
                // Si hubo un error
                is ResultadoOperacion.Error -> {
                    // Oculta el loader
                    mostrarCarga(false)
                    // Muestra el motivo del error
                    Toast.makeText(this@MainActivity, getString(resultado.mensajeId), Toast.LENGTH_LONG).show()
                    // Cierra el caso de error
                }
                // Cierra el when
            }
            // Cierra la corrutina
        }
        // Cierra la función
    }

    // Abre un enlace en el navegador
    private fun abrirEnlace(enlace: String) {
        // Intenta abrir el enlace
        try {
            // Lanza la pantalla del navegador con la dirección
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(enlace)))
            // Si no hay app que pueda abrirlo
        } catch (e: ActivityNotFoundException) {
            // Avisa al usuario
            Toast.makeText(this, getString(R.string.error_abrir_enlace), Toast.LENGTH_SHORT).show()
            // Cierra el catch
        }
        // Cierra la función
    }

    // Cierra la sesión y regresa al login
    private fun cerrarSesion() {
        // Borra la sesión guardada
        sesion.cerrarSesion()
        // Abre el login limpiando el historial de pantallas
        startActivity(Intent(this, LoginActivity::class.java).apply {
            // Evita que el botón "atrás" regrese a la pantalla principal
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Cierra el apply
        })
        // Cierra la función
    }
// Cierra la clase
}