package com.example.monsterapp

import android.app.Application
import android.util.Log
import com.example.monsterapp.data.local.AppDatabase
import com.example.monsterapp.data.repository.PokemonRepository
import org.osmdroid.config.Configuration

/**
 * Clase Application personalizada para MonsterApp.
 *
 * Se ejecuta al arrancar la app, antes de cualquier Activity o Fragment.
 * Es el lugar ideal para:
 * - Inicializar la base de datos Room (Singleton)
 * - Crear el repositorio (Singleton)
 * - Configurar OSMDroid
 *
 * Usamos 'by lazy' para que los objetos no consuman memoria hasta que se usen.
 */
class MonsterExplorerApp : Application() {

    /**
     * Instancia única de la base de datos Room.
     * Se crea de forma lazy (solo cuando se accede por primera vez).
     */
    val database by lazy {
        Log.d("MonsterExplorerApp", "📦 Inicializando base de datos Room...")
        AppDatabase.getDatabase(this)
    }

    /**
     * Instancia única del repositorio.
     * Recibe el DAO de la base de datos para operaciones locales.
     * [MEJORA] Ahora también recibe el DAO de historial de combates e inventario
     */
    val repository by lazy {
        Log.d("MonsterExplorerApp", "🔗 Inicializando repositorio...")
        PokemonRepository(
            database.pokemonDao(),
            database.battleHistoryDao(),
            database.inventarioDao()
        )
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("MonsterExplorerApp", "🚀 Inicializando aplicación MonsterApp...")

        // Configurar OSMDroid
        configurarOSMDroid()
    }

    /**
     * Configura OSMDroid para que funcione correctamente.
     *
     * OpenStreetMap requiere que las apps se identifiquen con un user-agent.
     * Sin esta configuración, el mapa puede no cargar.
     */
    private fun configurarOSMDroid() {
        val config = Configuration.getInstance()

        // Establecer el user-agent (OBLIGATORIO para OpenStreetMap)
        config.userAgentValue = packageName

        // Configurar directorios de caché
        config.osmdroidBasePath = filesDir
        config.osmdroidTileCache = cacheDir

        Log.d("MonsterExplorerApp", "✅ OSMDroid configurado con user-agent: $packageName")
    }
}

