// Declara el paquete de utilidades
package com.example.aprendeapp.utils

// Importa la clase R para acceder a los textos
import com.example.aprendeapp.R

// Object con los tipos de recurso válidos y su texto visible
object TiposRecurso {
    // Valor del tipo libro tal como se guarda en la API
    const val LIBRO = "libro"
    // Valor del tipo video tal como se guarda en la API
    const val VIDEO = "video"
    // Valor del tipo artículo tal como se guarda en la API (sin tilde, igual que en MockAPI)
    const val ARTICULO = "articulo"
    // Valor del tipo tutorial tal como se guarda en la API
    const val TUTORIAL = "tutorial"

    // Lista con todos los tipos (la usaremos en el formulario del docente)
    val TODOS = listOf(LIBRO, VIDEO, ARTICULO, TUTORIAL)

    // Devuelve el id del texto visible que corresponde a un tipo
    fun etiqueta(tipo: String): Int = when (tipo.lowercase()) {
        // Si es libro, usa el texto "Libro"
        LIBRO -> R.string.tipo_libro
        // Si es video, usa el texto "Video"
        VIDEO -> R.string.tipo_video
        // Si es artículo, usa el texto "Artículo"
        ARTICULO -> R.string.tipo_articulo
        // Si es tutorial, usa el texto "Tutorial"
        TUTORIAL -> R.string.tipo_tutorial
        // Cualquier otro valor usa el texto "Otro"
        else -> R.string.tipo_otro
        // Cierra el when
    }
// Cierra el objeto
}