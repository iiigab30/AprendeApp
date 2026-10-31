plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.aprendeapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.aprendeapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Abre el bloque de características de compilación
    buildFeatures {
        // Activa ViewBinding para acceder a las vistas de forma segura
        viewBinding = true
// Cierra el bloque buildFeatures
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // Cliente HTTP para consumir la API REST
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Conversor de JSON a objetos Kotlin usando Gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Registra las peticiones HTTP en Logcat
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Librería para cargar imágenes desde una URL
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Componentes de Material Design
    implementation("com.google.android.material:material:1.11.0")
    // Componente RecyclerView para mostrar listas de tarjetas
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // Corrutinas de Kotlin para ejecutar tareas en segundo plano en Android
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // Proporciona lifecycleScope para lanzar corrutinas ligadas al ciclo de vida de la pantalla
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
}