// Declara el paquete donde vive esta clase
package com.example.aprendeapp.model
// Importa la interfaz que permite enviar el objeto entre pantallas
import java.io.Serializable

// Clase de datos que representa un recurso de aprendizaje de la API
data class Recurso(
    // Identificador generado por MockAPI (es String y es nulo al crear uno nuevo)
    val id: String? = null,
    // Título del recurso
    val titulo: String,
    // Descripción detallada del recurso
    val descripcion: String,
    // Tipo de recurso: libro, video, articulo o tutorial
    val tipo: String,
    // URL para acceder al recurso completo
    val enlace: String,
    // URL de la imagen ilustrativa
    val imagen: String,
    // Suma de todas las estrellas recibidas (var porque se actualiza al calificar)
    var sumaCalificaciones: Int = 0,
    // Cantidad de votos recibidos (var porque se actualiza al calificar)
    var numeroVotos: Int = 0
// Cierra la lista de parámetros y abre el cuerpo de la clase
) : Serializable {
    // Propiedad calculada: promedio de estrellas (no se envía a la API porque no tiene campo propio)
    val promedio: Double
        // Si no hay votos devuelve 0.0 para evitar dividir entre cero; si hay, calcula el promedio
        get() = if (numeroVotos == 0) 0.0 else sumaCalificaciones.toDouble() / numeroVotos
// Cierra la clase
}