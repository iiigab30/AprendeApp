// Declara el paquete donde vive esta interfaz
package com.example.aprendeapp.model

// Importa la anotación para el metodo DELETE
import retrofit2.http.DELETE
// Importa la anotación para el metodo GET
import retrofit2.http.GET
// Importa la anotación para enviar un objeto en el cuerpo de la petición
import retrofit2.http.Body
// Importa la anotación para el metodo POST
import retrofit2.http.POST
// Importa la anotación para el metodo PUT
import retrofit2.http.PUT
// Importa la anotación para reemplazar una parte variable de la ruta
import retrofit2.http.Path
// Importa la anotación para agregar parámetros a la URL (?clave=valor)
import retrofit2.http.Query

// Interfaz que describe todas las peticiones HTTP hacia MockAPI
interface ApiService {

    // GET /recursos: obtiene la lista completa de recursos
    @GET("recursos")
    // suspend permite llamarla desde una corrutina sin bloquear la pantalla
    suspend fun obtenerRecursos(): List<Recurso>

    // GET /recursos/{id}: obtiene un recurso específico
    @GET("recursos/{id}")
    // El valor de id se inserta en la ruta
    suspend fun obtenerRecurso(@Path("id") id: String): Recurso

    // POST /recursos: crea un recurso nuevo (solo docente)
    @POST("recursos")
    // El objeto Recurso se envía como JSON en el cuerpo
    suspend fun crearRecurso(@Body recurso: Recurso): Recurso

    // PUT /recursos/{id}: edita un recurso existente o actualiza su calificación
    @PUT("recursos/{id}")
    // Recibe el id en la ruta y el recurso modificado en el cuerpo
    suspend fun actualizarRecurso(@Path("id") id: String, @Body recurso: Recurso): Recurso

    // DELETE /recursos/{id}: elimina un recurso (solo docente)
    @DELETE("recursos/{id}")
    // MockAPI devuelve el recurso eliminado
    suspend fun eliminarRecurso(@Path("id") id: String): Recurso

    // POST /usuarios: registra un usuario nuevo
    @POST("usuarios")
    // El objeto Usuario se envía como JSON en el cuerpo
    suspend fun registrarUsuario(@Body usuario: Usuario): Usuario

    // GET /usuarios?correo=...: busca usuarios por correo (para el login)
    @GET("usuarios")
    // Devuelve una lista con los usuarios que coinciden con el correo
    suspend fun buscarUsuarioPorCorreo(@Query("correo") correo: String): List<Usuario>
// Cierra la interfaz
}