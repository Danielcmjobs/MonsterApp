package com.example.monsterapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * [MEJORA OPCIONAL - No forma parte de la práctica]
 *
 * Utilidades para formatear fechas de forma amigable.
 * Convierte timestamps en textos legibles como "Hace 5 minutos".
 */
object DateUtils {

    /**
     * Convierte un timestamp en un texto de tiempo relativo.
     *
     * Ejemplos:
     * - "Hace un momento" (menos de 1 minuto)
     * - "Hace 5 minutos"
     * - "Hace 2 horas"
     * - "Hace 3 días"
     * - "15/02/2024" (más de 7 días)
     *
     * @param timestamp Timestamp en milisegundos
     * @return Texto formateado del tiempo transcurrido
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Hace un momento"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "Hace $minutes ${if (minutes == 1L) "minuto" else "minutos"}"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "Hace $hours ${if (hours == 1L) "hora" else "horas"}"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "Hace $days ${if (days == 1L) "día" else "días"}"
            }
            else -> {
                // Más de una semana, mostrar fecha completa
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateFormat.format(Date(timestamp))
            }
        }
    }

    /**
     * Formatea un timestamp como fecha y hora completa.
     *
     * @param timestamp Timestamp en milisegundos
     * @return Texto formateado (ej: "15/02/2024 14:30")
     */
    fun formatDateTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}

