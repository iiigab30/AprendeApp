// Declara el paquete donde vive esta clase
package com.example.aprendeapp.model

// Clase de datos que representa un usuario registrado
data class Usuario(
    // Identificador generado por MockAPI (nulo al registrar un usuario nuevo)
    val id: String? = null,
    // Nombre completo del usuario
    val nombre: String,
    // Correo electrónico, se usa para iniciar sesión
    val correo: String,
    // Contraseña guardada como hash SHA-256 (nunca en texto plano)
    val password: String,
    // Rol del usuario: "estudiante" o "docente"
    val rol: String
// Cierra la clase
)