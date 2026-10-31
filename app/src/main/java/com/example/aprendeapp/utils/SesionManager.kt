// Declara el paquete de utilidades
package com.example.aprendeapp.utils

// Importa Context para acceder a SharedPreferences
import android.content.Context
// Importa el modelo de usuario
import com.example.aprendeapp.model.Usuario

// Clase que administra la sesión del usuario en almacenamiento local
class SesionManager(context: Context) {
    // Archivo local privado donde se guarda la sesión
    private val prefs = context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE)

    // Guarda los datos del usuario que acaba de iniciar sesión
    fun guardarSesion(usuario: Usuario) {
        // Abre el editor de preferencias
        prefs.edit()
            // Guarda el id del usuario
            .putString("id", usuario.id)
            // Guarda el nombre del usuario
            .putString("nombre", usuario.nombre)
            // Guarda el rol del usuario
            .putString("rol", usuario.rol)
            // Aplica los cambios en segundo plano
            .apply()
        // Cierra la función
    }

    // Indica si hay una sesión activa (existe un id guardado)
    fun estaLogueado(): Boolean = prefs.getString("id", null) != null

    // Devuelve el id del usuario o vacío si no hay sesión
    fun obtenerId(): String = prefs.getString("id", "") ?: ""

    // Devuelve el nombre del usuario o vacío si no hay sesión
    fun obtenerNombre(): String = prefs.getString("nombre", "") ?: ""

    // Devuelve el rol del usuario o vacío si no hay sesión
    fun obtenerRol(): String = prefs.getString("rol", "") ?: ""

    // Indica si el usuario actual es docente
    fun esDocente(): Boolean = obtenerRol() == Constantes.ROL_DOCENTE

    // Cierra la sesión borrando todos los datos guardados
    fun cerrarSesion() {
        // Limpia las preferencias y aplica el cambio
        prefs.edit().clear().apply()
        // Cierra la función
    }
// Cierra la clase
}