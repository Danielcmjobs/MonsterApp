package com.example.monsterapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [MEJORA] Entidad de Room para el inventario del jugador.
 *
 * Almacena la cantidad de items que tiene el jugador.
 * Por ahora solo contiene "curas" pero se puede expandir.
 *
 * @property id ID único (siempre será 1, solo hay un inventario)
 * @property cantidadCuras Número de pociones/curas disponibles
 */
@Entity(tableName = "inventario")
data class InventarioEntity(
    @PrimaryKey
    val id: Int = 1,  // Solo hay un registro de inventario

    val cantidadCuras: Int = 0  // Pociones disponibles para curar Pokémon heridos
)

