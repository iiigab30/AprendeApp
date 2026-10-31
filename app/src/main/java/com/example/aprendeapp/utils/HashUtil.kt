// Declara el paquete de utilidades
package com.example.aprendeapp.utils

// Importa la clase de Java que calcula hashes
import java.security.MessageDigest

// Object con la función para cifrar contraseñas
object HashUtil {
    // Recibe un texto y devuelve su hash SHA-256 en hexadecimal
    fun sha256(texto: String): String {
        // Calcula el hash del texto convertido a bytes UTF-8
        val bytes = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray(Charsets.UTF_8))
        // Convierte cada byte a dos dígitos hexadecimales y los une en un solo texto
        return bytes.joinToString("") { "%02x".format(it) }
        // Cierra la función
    }
// Cierra el objeto
}