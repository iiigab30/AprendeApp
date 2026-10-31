// Declara el paquete de utilidades
package com.example.aprendeapp.utils

// Importa la clase R para acceder a los textos de strings.xml
import com.example.aprendeapp.R

// Object que valida las reglas de contraseña del desafío
object ValidadorPassword {
    // Longitud mínima exigida por el PDF
    private const val LONGITUD_MINIMA = 12
    // Caracteres especiales permitidos por el PDF (el $ se escapa con \)
    private const val ESPECIALES = "!@#\$%^&*"

    // Devuelve la lista de textos de error; si está vacía, la contraseña es válida
    fun validar(password: String): List<Int> {
        // Lista mutable donde se acumulan los errores encontrados
        val errores = mutableListOf<Int>()
        // Regla 1: mínimo 12 caracteres
        if (password.length < LONGITUD_MINIMA) errores.add(R.string.error_password_corta)
        // Regla 2: al menos una mayúscula
        if (!password.any { it.isUpperCase() }) errores.add(R.string.error_password_mayuscula)
        // Regla 3: al menos una minúscula
        if (!password.any { it.isLowerCase() }) errores.add(R.string.error_password_minuscula)
        // Regla 4: al menos un número
        if (!password.any { it.isDigit() }) errores.add(R.string.error_password_numero)
        // Regla 5: al menos un carácter especial de la lista permitida
        if (!password.any { it in ESPECIALES }) errores.add(R.string.error_password_especial)
        // Devuelve todos los errores encontrados
        return errores
        // Cierra la función
    }
// Cierra el objeto
}