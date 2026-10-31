// Declara el paquete de utilidades
package com.example.aprendeapp.utils

// Importa Context para acceder a SharedPreferences
import android.content.Context

// Guarda los favoritos y los votos de un usuario en el celular (un archivo por usuario)
class InteraccionManager(context: Context, usuarioId: String) {

    // Archivo local privado, distinto para cada usuario
    private val prefs = context.getSharedPreferences("interacciones_$usuarioId", Context.MODE_PRIVATE)

    // Devuelve una copia de los ids favoritos (Android no permite modificar el set original)
    fun obtenerFavoritos(): Set<String> = prefs.getStringSet("favoritos", emptySet())?.toSet() ?: emptySet()

    // Indica si un recurso está marcado como favorito
    fun esFavorito(id: String): Boolean = obtenerFavoritos().contains(id)

    // Marca o desmarca un favorito y devuelve el nuevo estado (true = ahora es favorito)
    fun alternarFavorito(id: String): Boolean {
        // Copia editable de los favoritos actuales
        val favoritos = obtenerFavoritos().toMutableSet()
        // Si ya estaba, lo quita; si no, lo agrega
        val ahoraEsFavorito = if (favoritos.contains(id)) {
            // Quita el id de la lista
            favoritos.remove(id)
            // Ya no es favorito
            false
            // Si no estaba en la lista
        } else {
            // Agrega el id a la lista
            favoritos.add(id)
            // Ahora es favorito
            true
            // Cierra el if
        }
        // Guarda la lista actualizada
        prefs.edit().putStringSet("favoritos", favoritos).apply()
        // Devuelve el nuevo estado
        return ahoraEsFavorito
        // Cierra la función
    }

    // Devuelve el voto que este usuario dio a un recurso (0 si no ha votado)
    fun obtenerCalificacion(id: String): Int = prefs.getInt("cal_$id", 0)

    // Guarda el voto que este usuario dio a un recurso
    fun guardarCalificacion(id: String, estrellas: Int) {
        // Guarda el número de estrellas y aplica el cambio
        prefs.edit().putInt("cal_$id", estrellas).apply()
        // Cierra la función
    }
// Cierra la clase
}