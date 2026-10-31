// Declara el paquete de las vistas
package com.example.aprendeapp.view

// Importa LayoutInflater para crear las tarjetas
import android.view.LayoutInflater
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
    // Función que se ejecuta cuando el usuario toca el enlace de una tarjeta
    private val alAbrirEnlace: (Recurso) -> Unit
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
            // Cierra el with
        }
        // Cierra la función
    }
// Cierra la clase
}