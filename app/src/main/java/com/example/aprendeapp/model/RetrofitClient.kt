// Declara el paquete donde vive este objeto
package com.example.aprendeapp.model

// Importa el interceptor que imprime las peticiones en Logcat
import okhttp3.logging.HttpLoggingInterceptor
// Importa el cliente HTTP sobre el que funciona Retrofit
import okhttp3.OkHttpClient
// Importa el constructor principal de Retrofit
import retrofit2.Retrofit
// Importa el conversor de JSON a objetos Kotlin
import retrofit2.converter.gson.GsonConverterFactory

// Object = una única instancia compartida (singleton) en toda la app
object RetrofitClient {

    // URL base de tu proyecto en MockAPI (debe terminar en "/")
    private const val BASE_URL = "https://6ae17d23be3bc8fead988df6.mockapi.io/api/v1/"

    // Crea el interceptor que registra el detalle de cada petición
    private val logging = HttpLoggingInterceptor().apply {
        // Nivel BODY: muestra la URL, el cuerpo enviado y el cuerpo recibido
        level = HttpLoggingInterceptor.Level.BODY
        // Cierra el bloque apply
    }

    // Crea el cliente HTTP y le agrega el interceptor
    private val clienteHttp = OkHttpClient.Builder()
        // Registra el interceptor de logs
        .addInterceptor(logging)
        // Construye el cliente
        .build()

    // Instancia de ApiService que usará toda la app (se crea solo cuando se usa por primera vez)
    val api: ApiService by lazy {
        // Empieza a construir Retrofit
        Retrofit.Builder()
            // Indica la URL base de la API
            .baseUrl(BASE_URL)
            // Asigna el cliente HTTP con logs
            .client(clienteHttp)
            // Indica que el JSON se convierte con Gson
            .addConverterFactory(GsonConverterFactory.create())
            // Construye el objeto Retrofit
            .build()
            // Genera la implementación de la interfaz ApiService
            .create(ApiService::class.java)
        // Cierra el bloque lazy
    }
// Cierra el objeto
}