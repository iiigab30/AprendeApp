// Declara el paquete de controladores
package com.example.aprendeapp.controller

// Importa Normalizer para quitar las tildes de los textos
import java.text.Normalizer
// Importa la clase R para los textos de error
import com.example.aprendeapp.R
// Importa la interfaz con los endpoints
import com.example.aprendeapp.model.ApiService
// Importa el modelo de recurso
import com.example.aprendeapp.model.Recurso
// Importa el cliente de Retrofit
import com.example.aprendeapp.model.RetrofitClient

// Resultado posible de cargar la lista de recursos
sealed class ResultadoRecursos {
    // Carga correcta: trae la lista completa
    data class Exito(val recursos: List<Recurso>) : ResultadoRecursos()
    // Carga fallida: trae el id del texto de error
    data class Error(val mensajeId: Int) : ResultadoRecursos()
// Cierra la clase sellada
}

// Campos en los que se puede buscar (el orden debe coincidir con R.array.campos_busqueda)
enum class CampoBusqueda { TODO, ID, TITULO, TIPO }

// Criterios de orden (el orden debe coincidir con R.array.opciones_orden)
enum class Orden { TITULO_ASC, TITULO_DESC, TIPO, MEJOR_CALIFICADOS }

// Resultado posible de crear, actualizar o eliminar un recurso
sealed class ResultadoOperacion {
    // Operación correcta (no necesita datos adicionales)
    object Exito : ResultadoOperacion()
    // Operación fallida: trae el id del texto de error a mostrar
    data class Error(val mensajeId: Int) : ResultadoOperacion()
// Cierra la clase sellada
}

// Controlador con la lógica de la lista de recursos
class RecursoController(private val api: ApiService = RetrofitClient.api) {

    // Pide todos los recursos a la API
    suspend fun obtenerRecursos(): ResultadoRecursos {
        // Intenta consultar la API
        return try {
            // Devuelve la lista recibida
            ResultadoRecursos.Exito(api.obtenerRecursos())
            // Si falla la red o la API
        } catch (e: Exception) {
            // Devuelve el error de carga
            ResultadoRecursos.Error(R.string.error_cargar_recursos)
            // Cierra el catch
        }
        // Cierra la función
    }

    // Quita tildes y pasa a minúsculas para comparar textos
    private fun normalizar(texto: String): String {
        // Separa cada letra de su tilde
        val descompuesto = Normalizer.normalize(texto, Normalizer.Form.NFD)
        // Elimina las tildes y convierte a minúsculas
        return descompuesto.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").lowercase()
        // Cierra la función
    }

    // Aplica búsqueda, filtro por tipo y orden sobre la lista completa
    fun filtrarYOrdenar(
        // Lista completa recibida de la API
        recursos: List<Recurso>,
        // Texto escrito en el buscador
        consulta: String,
        // Campo donde se busca
        campo: CampoBusqueda,
        // Tipo seleccionado en los chips (null significa todos)
        tipo: String?,
        // Criterio de orden elegido
        orden: Orden
        // Devuelve la lista ya filtrada y ordenada
    ): List<Recurso> {
        // Normaliza el texto buscado
        val texto = normalizar(consulta.trim())
        // Conserva solo los recursos que cumplen búsqueda y filtro
        val filtrados = recursos.filter { recurso ->
            // Cumple el filtro de tipo si no hay tipo elegido o coincide exactamente
            val coincideTipo = tipo == null || recurso.tipo.equals(tipo, ignoreCase = true)
            // Cumple la búsqueda si no hay texto o coincide según el campo elegido
            val coincideTexto = texto.isEmpty() || when (campo) {
                // Por ID: coincidencia exacta
                CampoBusqueda.ID -> recurso.id == texto
                // Por título: el título contiene el texto
                CampoBusqueda.TITULO -> normalizar(recurso.titulo).contains(texto)
                // Por tipo: el tipo contiene el texto
                CampoBusqueda.TIPO -> normalizar(recurso.tipo).contains(texto)
                // En todo: coincide con el ID, el título o el tipo
                CampoBusqueda.TODO -> recurso.id == texto ||
                        normalizar(recurso.titulo).contains(texto) ||
                        normalizar(recurso.tipo).contains(texto)
                // Cierra el when
            }
            // Debe cumplir ambas condiciones
            coincideTipo && coincideTexto
            // Cierra el filter
        }
        // Ordena según el criterio elegido
        return when (orden) {
            // Título de la A a la Z
            Orden.TITULO_ASC -> filtrados.sortedBy { normalizar(it.titulo) }
            // Título de la Z a la A
            Orden.TITULO_DESC -> filtrados.sortedByDescending { normalizar(it.titulo) }
            // Por tipo y, dentro de cada tipo, por título
            Orden.TIPO -> filtrados.sortedWith(compareBy({ normalizar(it.tipo) }, { normalizar(it.titulo) }))
            // De mayor a menor promedio de calificación
            Orden.MEJOR_CALIFICADOS -> filtrados.sortedByDescending { it.promedio }
            // Cierra el when
        }
        // Cierra la función
    }
// Cierra la clase
// Crea un recurso nuevo en la API (POST)
suspend fun crear(recurso: Recurso): ResultadoOperacion {
    // Intenta ejecutar la petición
    return try {
        // Envía el recurso a la API; MockAPI le asigna el id automáticamente
        api.crearRecurso(recurso)
        // Indica que todo salió bien
        ResultadoOperacion.Exito
        // Si falla la red o la API
    } catch (e: Exception) {
        // Devuelve el error de guardado
        ResultadoOperacion.Error(R.string.error_guardar_recurso)
        // Cierra el catch
    }
    // Cierra la función
}

    // Actualiza un recurso existente en la API (PUT)
    suspend fun actualizar(recurso: Recurso): ResultadoOperacion {
        // Intenta ejecutar la petición
        return try {
            // Toma el id del recurso; si no tiene, devuelve error
            val id = recurso.id ?: return ResultadoOperacion.Error(R.string.error_guardar_recurso)
            // Envía los datos nuevos a la API
            api.actualizarRecurso(id, recurso)
            // Indica que todo salió bien
            ResultadoOperacion.Exito
            // Si falla la red o la API
        } catch (e: Exception) {
            // Devuelve el error de guardado
            ResultadoOperacion.Error(R.string.error_guardar_recurso)
            // Cierra el catch
        }
        // Cierra la función
    }

    // Elimina un recurso de la API (DELETE)
    suspend fun eliminar(id: String): ResultadoOperacion {
        // Intenta ejecutar la petición
        return try {
            // Pide a la API que elimine el recurso
            api.eliminarRecurso(id)
            // Indica que todo salio bien
            ResultadoOperacion.Exito
            // Si falla la red o la API
        } catch (e: Exception) {
            // Devuelve el error de eliminación
            ResultadoOperacion.Error(R.string.error_eliminar_recurso)
            // Cierra el catch
        }
        // Cierra la función
    }

    // Indica si un texto es una URL válida que empieza con http:// o https://
    fun esUrlValida(url: String): Boolean {
        // Debe empezar con http:// o https://
        val tieneProtocolo = url.startsWith("http://") || url.startsWith("https://")
        // Debe cumplir el patrón de dirección web de Android
        return tieneProtocolo && android.util.Patterns.WEB_URL.matcher(url).matches()
        // Cierra la función
    }


}