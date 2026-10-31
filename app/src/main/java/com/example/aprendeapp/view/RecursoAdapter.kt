// Declara el paquete de las vistas
package com.example.aprendeapp.view

// Importa LayoutInflater para crear las tarjetas
import android.view.LayoutInflater
// Importa View para controlar visibilidades
import android.view.View
// Importa ViewGroup, el contenedor padre de cada tarjeta
import android.view.ViewGroup
// Importa DiffUtil para actualizar la lista solo con los cambios
import androidx.recyclerview.widget.DiffUtil
// Importa ListAdapter, un adaptador que usa DiffUtil
import androidx.recyclerview.widget.ListAdapter
// Importa RecyclerView
import androidx.recyclerview.widget.RecyclerView
// Importa Glide para cargar imágenes desde una URL
import com.bumptech.glide.Glide
// Importa la clase R de la app
import com.example.aprendeapp.R
// Importa el binding generado a partir de item_recurso.xml
import com.example.aprendeapp.databinding.ItemRecursoBinding
// Importa el modelo de recurso
import com.example.aprendeapp.model.Recurso
// Importa la utilidad de tipos
import com.example.aprendeapp.utils.TiposRecurso

// Adaptador que dibuja cada recurso como una tarjeta
class RecursoAdapter(
    // Indica si el usuario actual es docente (muestra editar/eliminar; si no, favorito/calificar)
    private val esDocente: Boolean,
    // Se ejecuta cuando el usuario toca el enlace de una tarjeta
    private val alAbrirEnlace: (Recurso) -> Unit,
    // Se ejecuta cuando el docente pulsa "Editar"
    private val alEditar: (Recurso) -> Unit,
    // Se ejecuta cuando el docente pulsa "Eliminar"
    private val alEliminar: (Recurso) -> Unit,
    // Pregunta si un recurso es favorito del usuario actual
    private val esFavorito: (Recurso) -> Boolean,
    // Marca o desmarca un favorito y devuelve el nuevo estado
    private val alAlternarFavorito: (Recurso) -> Boolean,
    // Devuelve el voto del usuario actual a un recurso (0 si no ha votado)
    private val miCalificacion: (Recurso) -> Int,
    // Se ejecuta cuando el estudiante pulsa "Calificar"
    private val alCalificar: (Recurso) -> Unit
// Hereda de ListAdapter indicando el modelo y el ViewHolder
) : ListAdapter<Recurso, RecursoAdapter.RecursoViewHolder>(Comparador) {

    // Guarda las vistas de una tarjeta para reutilizarlas al hacer scroll
    class RecursoViewHolder(val binding: ItemRecursoBinding) : RecyclerView.ViewHolder(binding.root)

    // Comparador que le dice al adaptador qué elementos cambiaron
    object Comparador : DiffUtil.ItemCallback<Recurso>() {
        // Dos recursos son el mismo elemento si tienen el mismo id
        override fun areItemsTheSame(anterior: Recurso, nuevo: Recurso) = anterior.id == nuevo.id
        // Tienen el mismo contenido si todos sus datos son iguales
        override fun areContentsTheSame(anterior: Recurso, nuevo: Recurso) = anterior == nuevo
        // Cierra el comparador
    }

    // Devuelve el ícono de estrella llena o vacía según el estado de favorito
    private fun iconoFavorito(activo: Boolean): Int =
        // Estrella amarilla si es favorito, estrella gris si no
        if (activo) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off

    // Crea una tarjeta vacía
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecursoViewHolder {
        // Infla el layout de la tarjeta
        val binding = ItemRecursoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Devuelve el ViewHolder con ese layout
        return RecursoViewHolder(binding)
        // Cierra la función
    }

    // Llena una tarjeta con los datos de un recurso
    override fun onBindViewHolder(holder: RecursoViewHolder, position: Int) {
        // Obtiene el recurso que corresponde a esta posición
        val recurso = getItem(position)
        // Obtiene el contexto para leer textos de strings.xml
        val contexto = holder.itemView.context
        // Usa el binding de la tarjeta para no repetir "holder.binding"
        with(holder.binding) {
            // Muestra el título
            tvTitulo.text = recurso.titulo
            // Muestra la descripción
            tvDescripcion.text = recurso.descripcion
            // Muestra el tipo con su texto visible
            tvTipo.text = contexto.getString(TiposRecurso.etiqueta(recurso.tipo))
            // Muestra el ID (o un guion si no tiene)
            tvId.text = contexto.getString(R.string.item_id, recurso.id ?: "-")
            // Muestra el promedio y la cantidad de votos
            tvCalificacion.text = contexto.getString(R.string.item_calificacion, recurso.promedio, recurso.numeroVotos)
            // Muestra el enlace
            tvEnlace.text = recurso.enlace
            // Al tocar el enlace, avisa a la pantalla para que lo abra
            tvEnlace.setOnClickListener { alAbrirEnlace(recurso) }
            // Carga la imagen desde la URL
            Glide.with(holder.itemView)
                // Indica la URL de la imagen
                .load(recurso.imagen)
                // Imagen temporal mientras descarga
                .placeholder(android.R.drawable.ic_menu_gallery)
                // Imagen que se muestra si la descarga falla
                .error(android.R.drawable.ic_menu_report_image)
                // Coloca el resultado en el ImageView
                .into(ivImagen)

            // Muestra la fila del docente solo si el usuario es docente
            llAcciones.visibility = if (esDocente) View.VISIBLE else View.GONE
            // Al pulsar "Editar" avisa a la pantalla con el recurso de esta tarjeta
            btnEditar.setOnClickListener { alEditar(recurso) }
            // Al pulsar "Eliminar" avisa a la pantalla con el recurso de esta tarjeta
            btnEliminar.setOnClickListener { alEliminar(recurso) }

            // Muestra la fila del estudiante solo si el usuario NO es docente
            llEstudiante.visibility = if (esDocente) View.GONE else View.VISIBLE
            // Dibuja la estrella llena o vacía según el estado actual del favorito
            ibFavorito.setImageResource(iconoFavorito(esFavorito(recurso)))
            // Al pulsar la estrella
            ibFavorito.setOnClickListener {
                // Marca o desmarca el favorito y obtiene el nuevo estado
                val ahoraEsFavorito = alAlternarFavorito(recurso)
                // Actualiza la estrella al instante
                ibFavorito.setImageResource(iconoFavorito(ahoraEsFavorito))
                // Cierra el listener
            }
            // Obtiene el voto que este usuario dio a este recurso
            val miVoto = miCalificacion(recurso)
            // Si ya votó muestra su voto; si no, muestra "Calificar"
            btnCalificar.text = if (miVoto > 0) contexto.getString(R.string.btn_calificar_mia, miVoto) else contexto.getString(R.string.btn_calificar)
            // Al pulsar el botón abre el diálogo de calificación
            btnCalificar.setOnClickListener { alCalificar(recurso) }
            // Cierra el with
        }
        // Cierra la función
    }
// Cierra la clase
}