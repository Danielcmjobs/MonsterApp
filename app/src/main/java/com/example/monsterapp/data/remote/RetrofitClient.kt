package com.example.monsterapp.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import android.util.Log

/**
 * Objeto Singleton que configura y proporciona la instancia de Retrofit.
 * Usamos object para tener una única instancia en toda la app.
 */
object RetrofitClient {

    // URL base de PokeAPI - todos los endpoints parten de aquí
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    // TAG para los logs
    private const val TAG = "RetrofitClient"

    /**
     * Configuración de Moshi para parsear JSON.
     * KotlinJsonAdapterFactory permite trabajar con data classes de Kotlin.
     */
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * Interceptor para logging de peticiones HTTP.
     * Nivel BODY muestra todo: headers, body de request y response.
     * Muy útil para depurar durante el desarrollo.
     */
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        // Personalizamos el log para que sea más fácil de identificar
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente OkHttp configurado con el interceptor de logging.
     * OkHttp es el cliente HTTP que usa Retrofit internamente.
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Instancia de PokeApiService creada de forma lazy (solo cuando se necesita).
     * Esta es la interfaz que usaremos para hacer las llamadas a la API.
     */
    val api: PokeApiService by lazy {
        Log.d(TAG, "Inicializando Retrofit con URL base: $BASE_URL")

        Retrofit.Builder()
            .baseUrl(BASE_URL)                                    // URL base de la API
            .client(client)                                       // Cliente OkHttp con logging
            .addConverterFactory(MoshiConverterFactory.create(moshi))  // Conversor JSON
            .build()
            .create(PokeApiService::class.java)                   // Crear implementación de la interfaz
    }
}

