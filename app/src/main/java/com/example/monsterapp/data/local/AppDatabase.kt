package com.example.monsterapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.monsterapp.data.local.dao.BattleHistoryDao
import com.example.monsterapp.data.local.dao.InventarioDao
import com.example.monsterapp.data.local.dao.PokemonDao
import com.example.monsterapp.data.local.entities.BattleHistoryEntity
import com.example.monsterapp.data.local.entities.InventarioEntity
import com.example.monsterapp.data.local.entities.PokemonEntity

/**
 * Base de datos principal de la aplicación usando Room.
 *
 * Esta clase abstracta actúa como el punto de acceso principal a la base de datos.
 * Utiliza el patrón Singleton para asegurar que solo existe una instancia
 * de la base de datos en toda la aplicación.
 *
 * @Database: Anotación que define las entidades y la versión de la BD
 * - entities: Lista de clases @Entity que forman las tablas
 * - version: Número de versión (incrementar cuando cambie el esquema)
 * - exportSchema: false para no exportar el esquema a un archivo
 */
@Database(
    entities = [PokemonEntity::class, BattleHistoryEntity::class, InventarioEntity::class],
    version = 3,  // Incrementado por: estaHerido en PokemonEntity + InventarioEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Proporciona acceso al DAO de Pokémon.
     * Room genera la implementación automáticamente.
     */
    abstract fun pokemonDao(): PokemonDao

    /**
     * [MEJORA] Proporciona acceso al DAO de historial de combates.
     */
    abstract fun battleHistoryDao(): BattleHistoryDao

    /**
     * [MEJORA] Proporciona acceso al DAO del inventario (curas).
     */
    abstract fun inventarioDao(): InventarioDao

    companion object {
        // @Volatile asegura que los cambios sean visibles inmediatamente en todos los hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos (Singleton).
         *
         * Si la instancia ya existe, la devuelve.
         * Si no existe, la crea de forma thread-safe usando synchronized.
         *
         * @param context Contexto de la aplicación
         * @return Instancia única de AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la devolvemos
            return INSTANCE ?: synchronized(this) {
                // Doble verificación dentro del bloque sincronizado
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pokemon_database"  // Nombre físico del archivo .db
                )
                // Opcional: Destruir y recrear si hay cambios de esquema (solo para desarrollo)
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

