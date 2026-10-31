// Declara el paquete de controladores
package com.example.aprendeapp.controller

// Importa la clase R para los textos de error
import com.example.aprendeapp.R
// Importa la interfaz con los endpoints
import com.example.aprendeapp.model.ApiService
// Importa el cliente de Retrofit
import com.example.aprendeapp.model.RetrofitClient
// Importa el modelo de usuario
import com.example.aprendeapp.model.Usuario
// Importa la utilidad de hash
import com.example.aprendeapp.utils.HashUtil
// Importa la excepción que lanza Retrofit con códigos HTTP de error
import retrofit2.HttpException

// Resultado posible de registrar o iniciar sesión
sealed class ResultadoAuth {
    // Operación correcta: trae el usuario resultante
    data class Exito(val usuario: Usuario) : ResultadoAuth()
    // Operación fallida: trae el id del texto de error a mostrar
    data class Error(val mensajeId: Int) : ResultadoAuth()
// Cierra la clase sellada
}

// Controlador que contiene la lógica de registro y login
class AuthController(private val api: ApiService = RetrofitClient.api) {

    // Busca un usuario cuyo correo coincida exactamente; devuelve null si no existe
    private suspend fun buscarPorCorreo(correo: String): Usuario? {
        // Intenta consultar la API
        return try {
            // Filtra la lista para quedarse solo con la coincidencia exacta (sin importar mayúsculas)
            api.buscarUsuarioPorCorreo(correo).firstOrNull { it.correo.equals(correo, ignoreCase = true) }
            // Captura errores HTTP
        } catch (e: HttpException) {
            // MockAPI devuelve 404 cuando no hay coincidencias: significa que no existe
            if (e.code() == 404) null else throw e
            // Cierra el catch
        }
        // Cierra la función
    }

    // Registra un usuario nuevo guardando su contraseña como hash
    suspend fun registrar(nombre: String, correo: String, password: String, rol: String): ResultadoAuth {
        // Intenta ejecutar el registro
        return try {
            // Si el correo ya existe, devuelve error
            if (buscarPorCorreo(correo) != null) return ResultadoAuth.Error(R.string.error_correo_existente)
            // Crea el usuario con la contraseña cifrada
            val nuevo = Usuario(nombre = nombre, correo = correo, password = HashUtil.sha256(password), rol = rol)
            // Envía el POST y devuelve el usuario creado por la API
            ResultadoAuth.Exito(api.registrarUsuario(nuevo))
            // Si falla la red o la API
        } catch (e: Exception) {
            // Devuelve error de conexión
            ResultadoAuth.Error(R.string.error_conexion)
            // Cierra el catch
        }
        // Cierra la función
    }

    // Inicia sesión comparando el hash de la contraseña ingresada con el guardado
    suspend fun login(correo: String, password: String): ResultadoAuth {
        // Intenta ejecutar el login
        return try {
            // Busca al usuario; si no existe devuelve error genérico
            val usuario = buscarPorCorreo(correo) ?: return ResultadoAuth.Error(R.string.error_credenciales)
            // Compara el hash guardado con el hash de la contraseña ingresada
            if (usuario.password != HashUtil.sha256(password)) return ResultadoAuth.Error(R.string.error_credenciales)
            // Si todo coincide, el login es exitoso
            ResultadoAuth.Exito(usuario)
            // Si falla la red o la API
        } catch (e: Exception) {
            // Devuelve error de conexión
            ResultadoAuth.Error(R.string.error_conexion)
            // Cierra el catch
        }
        // Cierra la función
    }
// Cierra la clase
}