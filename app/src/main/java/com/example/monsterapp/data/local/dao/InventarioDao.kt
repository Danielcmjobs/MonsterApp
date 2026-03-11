package com.example.monsterapp.data.local.dao

import androidx.room.*
import com.example.monsterapp.data.local.entities.InventarioEntity
import kotlinx.coroutines.flow.Flow

/**
 * [MEJORA] Data Access Object para el inventario del jugador.
 *
 * Gestiona las operaciones de curas/pociones.
 */
@Dao
interface InventarioDao {

    /**
     * Obtiene el inventario actual.
     * @return Flow con el inventario (se actualiza automáticamente)
     */
    @Query("SELECT * FROM inventario WHERE id = 1")
    fun obtenerInventario(): Flow<InventarioEntity?>

    /**
     * Obtiene el inventario de forma síncrona (para operaciones puntuales).
     */
    @Query("SELECT * FROM inventario WHERE id = 1")
    suspend fun obtenerInventarioSync(): InventarioEntity?

    /**
     * Obtiene solo la cantidad de curas.
     */
    @Query("SELECT cantidadCuras FROM inventario WHERE id = 1")
    fun obtenerCantidadCuras(): Flow<Int?>

    /**
     * Inserta o actualiza el inventario.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarInventario(inventario: InventarioEntity)

    /**
     * Añade curas al inventario.
     * @param cantidad Número de curas a añadir
     */
    @Query("UPDATE inventario SET cantidadCuras = cantidadCuras + :cantidad WHERE id = 1")
    suspend fun añadirCuras(cantidad: Int)

    /**
     * Usa una cura (resta 1 del inventario).
     * Solo si hay curas disponibles.
     */
    @Query("UPDATE inventario SET cantidadCuras = cantidadCuras - 1 WHERE id = 1 AND cantidadCuras > 0")
    suspend fun usarCura()

    /**
     * Inicializa el inventario si no existe.
     */
    @Query("INSERT OR IGNORE INTO inventario (id, cantidadCuras) VALUES (1, 0)")
    suspend fun inicializarInventario()
}

