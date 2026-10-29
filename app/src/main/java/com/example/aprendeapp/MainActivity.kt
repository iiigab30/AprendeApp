// Declara el paquete de la clase
package com.example.aprendeapp

// Importa la clase Bundle para el ciclo de vida de la Activity
import android.os.Bundle
// Importa Log para escribir mensajes en Logcat
import android.util.Log
// Importa la clase base de las pantallas
import androidx.appcompat.app.AppCompatActivity
// Importa lifecycleScope para lanzar corrutinas ligadas a la pantalla
import androidx.lifecycle.lifecycleScope
// Importa el cliente de Retrofit que creamos
import com.example.aprendeapp.model.RetrofitClient
// Importa launch para iniciar la corrutina
import kotlinx.coroutines.launch

// Pantalla principal (temporal, solo para probar la API)
class MainActivity : AppCompatActivity() {

    // Se ejecuta al crear la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la implementación base
        super.onCreate(savedInstanceState)
        // Carga el layout por defecto
        setContentView(R.layout.activity_main)

        // Lanza una corrutina para no bloquear la pantalla
        lifecycleScope.launch {
            // Intenta hacer la llamada a la API
            try {
                // Pide la lista de recursos a MockAPI
                val recursos = RetrofitClient.api.obtenerRecursos()
                // Muestra en Logcat cuántos recursos llegaron
                Log.d("API_TEST", "Recursos recibidos: ${recursos.size}")
                // Recorre cada recurso recibido
                recursos.forEach {
                    // Muestra el id y el título de cada recurso
                    Log.d("API_TEST", "${it.id} - ${it.titulo} (${it.tipo})")
                    // Cierra el forEach
                }
                // Si ocurre un error (sin internet, URL mala, etc.)
            } catch (e: Exception) {
                // Muestra el error en Logcat
                Log.e("API_TEST", "Error al conectar: ${e.message}")
                // Cierra el catch
            }
            // Cierra la corrutina
        }
        // Cierra onCreate
    }
// Cierra la clase
}