package com.example.monsterapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * [MEJORA] Helper para obtener la ubicación del usuario.
 * Permite cargar Pokémon alrededor de la ubicación real del usuario.
 */
class LocationHelper(private val context: Context) {

    private val TAG = "LocationHelper"
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    // Ubicación por defecto (Madrid) si no hay permisos o falla
    companion object {
        const val DEFAULT_LATITUDE = 40.4168
        const val DEFAULT_LONGITUDE = -3.7038
    }

    /**
     * Verifica si tenemos permisos de ubicación.
     */
    fun tienePermisosUbicacion(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Obtiene la última ubicación conocida (más rápido, menos preciso).
     * @return Location o null si no hay ubicación disponible
     */
    fun obtenerUltimaUbicacion(): Location? {
        if (!tienePermisosUbicacion()) {
            Log.w(TAG, "⚠️ No hay permisos de ubicación")
            return null
        }

        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Lista de proveedores disponibles
            val gpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
            val networkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false

            Log.d(TAG, "📡 Proveedores: GPS=$gpsEnabled, Network=$networkEnabled")

            var location: Location? = null
            var source = "ninguno"

            // 1. Intentar GPS primero (más preciso)
            if (gpsEnabled) {
                location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) source = "GPS"
                Log.d(TAG, "🛰️ GPS Location: ${location?.latitude}, ${location?.longitude}")
            }

            // 2. Si no hay GPS, intentar Network
            if ((location == null || !esUbicacionValida(location)) && networkEnabled) {
                val networkLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                Log.d(TAG, "📶 Network Location: ${networkLocation?.latitude}, ${networkLocation?.longitude}")

                // Usar network solo si GPS no está disponible o es inválido
                if (location == null || (networkLocation != null && esUbicacionValida(networkLocation))) {
                    location = networkLocation
                    source = "Network"
                }
            }

            // Validar que la ubicación sea razonable (en España/Europa)
            if (location != null && esUbicacionValida(location)) {
                Log.d(TAG, "✅ Ubicación válida ($source): ${location.latitude}, ${location.longitude}")
                Log.d(TAG, "📍 Precisión: ${location.accuracy} metros")
                return location
            } else {
                Log.w(TAG, "⚠️ Ubicación inválida o nula, usando Madrid por defecto")
                return null
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ Error de permisos: ${e.message}")
            return null
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al obtener ubicación: ${e.message}")
            return null
        }
    }

    /**
     * Solicita actualizaciones de ubicación en tiempo real.
     * @param onLocationUpdate Callback cuando se actualiza la ubicación
     */
    fun solicitarActualizacionesUbicacion(onLocationUpdate: (Location) -> Unit) {
        if (!tienePermisosUbicacion()) {
            Log.w(TAG, "⚠️ No hay permisos de ubicación")
            return
        }

        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    Log.d(TAG, "📍 Nueva ubicación: ${location.latitude}, ${location.longitude}")
                    onLocationUpdate(location)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            // Solicitar actualizaciones (cada 10 segundos o 10 metros)
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000L,  // 10 segundos
                10f,     // 10 metros
                locationListener!!
            )

            Log.d(TAG, "✅ Escuchando actualizaciones de ubicación")
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ Error de permisos: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al solicitar ubicación: ${e.message}")
        }
    }

    /**
     * Detiene las actualizaciones de ubicación.
     */
    fun detenerActualizaciones() {
        locationListener?.let {
            locationManager?.removeUpdates(it)
            Log.d(TAG, "🛑 Actualizaciones de ubicación detenidas")
        }
    }

    /**
     * [MEJORA] Valida que la ubicación sea razonable (en España aproximadamente).
     * Evita ubicaciones en el mar o en otros continentes.
     */
    private fun esUbicacionValida(location: Location): Boolean {
        val lat = location.latitude
        val lon = location.longitude

        // Validar que esté aproximadamente en España/Europa
        // España: Latitud 36-44, Longitud -10 a 5
        val enEspana = lat in 35.0..44.0 && lon in -10.0..5.0

        // También validar que no sea 0,0 (ubicación por defecto errónea)
        val noEsCero = !(lat == 0.0 && lon == 0.0)

        Log.d(TAG, "🔍 Validando ubicación: lat=$lat, lon=$lon, enEspaña=$enEspana, noEsCero=$noEsCero")

        return enEspana && noEsCero
    }

    /**
     * Obtiene la ubicación actual o la por defecto (Madrid centro).
     */
    fun obtenerUbicacionODefault(): Pair<Double, Double> {
        val location = obtenerUltimaUbicacion()
        return if (location != null) {
            Log.d(TAG, "📍 Usando ubicación real: ${location.latitude}, ${location.longitude}")
            Pair(location.latitude, location.longitude)
        } else {
            Log.d(TAG, "📍 Usando ubicación por defecto: Madrid")
            Pair(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        }
    }
}

