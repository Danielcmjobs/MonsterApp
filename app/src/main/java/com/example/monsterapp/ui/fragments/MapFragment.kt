package com.example.monsterapp.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.monsterapp.MonsterExplorerApp
import com.example.monsterapp.R
import com.example.monsterapp.data.model.PokemonResult
import com.example.monsterapp.databinding.FragmentMapBinding
import com.example.monsterapp.ui.viewmodel.MapViewModel
import com.example.monsterapp.ui.viewmodel.PokemonViewModelFactory
import com.example.monsterapp.utils.LocationHelper
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.random.Random

/**
 * Fragment que muestra el mapa con OSMDroid (OpenStreetMap).
 *
 * Funcionalidades:
 * - Muestra un mapa real de OpenStreetMap
 * - Carga Pokémon desde la API y los muestra como marcadores
 * - Al tocar un marcador, captura el Pokémon (lo guarda en Room)
 * - FAB para acceder a la Pokédex
 */
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // ViewModel con Factory para inyectar el repositorio
    private val viewModel: MapViewModel by viewModels {
        val app = requireActivity().application as MonsterExplorerApp
        PokemonViewModelFactory(app.repository)
    }

    // Referencia al mapa de OSMDroid
    private lateinit var map: MapView

    // [CORRECCIÓN] Set para rastrear marcadores ya capturados
    private val capturedMarkers = mutableSetOf<Marker>()

    // [MEJORA] Helper para obtener ubicación real del usuario
    private lateinit var locationHelper: LocationHelper

    // Punto central del mapa (se actualizará con la ubicación real)
    private var centerPoint = GeoPoint(40.4168, -3.7038)  // Madrid por defecto

    // [MEJORA] Launcher para solicitar permisos de ubicación
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d("MapFragment", "✅ Permisos de ubicación concedidos")
            obtenerUbicacionYCargarPokemon()
        } else {
            Log.w("MapFragment", "⚠️ Permisos de ubicación denegados, usando Madrid")
            Toast.makeText(requireContext(), "Usando ubicación por defecto (Madrid)", Toast.LENGTH_SHORT).show()
            cargarPokemonEnUbicacion()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MapFragment", "🗺️ Inicializando mapa OSMDroid...")

        // [MEJORA] Inicializar helper de ubicación
        locationHelper = LocationHelper(requireContext())

        // Configurar el mapa
        configurarMapa()

        // Configurar los botones flotantes
        configurarBotones()

        // Observar los datos del ViewModel
        observarViewModel()

        // [MEJORA] Verificar permisos y obtener ubicación
        verificarPermisosYCargar()
    }

    /**
     * [MEJORA] Verifica permisos de ubicación y carga Pokémon.
     */
    private fun verificarPermisosYCargar() {
        if (locationHelper.tienePermisosUbicacion()) {
            Log.d("MapFragment", "✅ Ya tenemos permisos de ubicación")
            obtenerUbicacionYCargarPokemon()
        } else {
            Log.d("MapFragment", "📍 Solicitando permisos de ubicación...")
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    /**
     * [MEJORA] Obtiene la ubicación real del usuario y carga los Pokémon.
     */
    private fun obtenerUbicacionYCargarPokemon() {
        val ubicacion = locationHelper.obtenerUbicacionODefault()
        centerPoint = GeoPoint(ubicacion.first, ubicacion.second)

        Log.d("MapFragment", "📍 Ubicación obtenida: ${centerPoint.latitude}, ${centerPoint.longitude}")

        // [DEBUG] Mostrar la ubicación que se está usando
        val esDefault = ubicacion.first == LocationHelper.DEFAULT_LATITUDE &&
                        ubicacion.second == LocationHelper.DEFAULT_LONGITUDE

        val mensaje = if (esDefault) {
            "📍 Sin GPS - Usando Madrid"
        } else {
            // Mostrar coordenadas abreviadas
            val lat = String.format("%.4f", ubicacion.first)
            val lon = String.format("%.4f", ubicacion.second)
            "📍 GPS: $lat, $lon"
        }
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()

        // Actualizar el mapa con la nueva ubicación
        map.controller.setCenter(centerPoint)

        // Cargar Pokémon alrededor de la ubicación
        cargarPokemonEnUbicacion()
    }

    /**
     * [MEJORA] Carga los Pokémon en la ubicación actual.
     */
    private fun cargarPokemonEnUbicacion() {
        viewModel.loadPokemon(20)
    }

    /**
     * Configura el mapa de OSMDroid con las opciones iniciales.
     */
    private fun configurarMapa() {
        map = binding.map

        // Habilitar controles multi-touch (zoom con pellizco)
        map.setMultiTouchControls(true)

        // Configurar zoom inicial
        map.controller.setZoom(15.0)

        // Centrar el mapa en el punto inicial
        map.controller.setCenter(centerPoint)

        Log.d("MapFragment", "✅ Mapa configurado - Centro: ${centerPoint.latitude}, ${centerPoint.longitude}")
    }

    /**
     * Configura los botones flotantes (FABs).
     */
    private fun configurarBotones() {
        // Botón para ir a favoritos/Pokédex
        binding.fabFavorites.setOnClickListener {
            navegarAFavoritos()
        }

        // Botón para centrar en ubicación
        binding.fabMyLocation.setOnClickListener {
            centrarEnUbicacion()
        }
    }

    /**
     * Observa los cambios en el ViewModel y actualiza la UI.
     */
    private fun observarViewModel() {
        // Observar la lista de Pokémon de la API
        viewModel.pokemonList.observe(viewLifecycleOwner) { pokemonList ->
            Log.d("MapFragment", "📍 Recibidos ${pokemonList.size} Pokémon")
            addPokemonMarkers(pokemonList)
            binding.tvNearbyCount.text = "${pokemonList.size} Pokémon cercanos"
        }

        // Observar capturas
        viewModel.pokemonCapturado.observe(viewLifecycleOwner) { nombre ->
            nombre?.let {
                Toast.makeText(
                    requireContext(),
                    "¡${it.replaceFirstChar { c -> c.uppercase() }} capturado! 🎉",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearCaptura()
            }
        }

        // Observar errores
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("MapFragment", "❌ Error: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    /**
     * Añade marcadores de Pokémon al mapa.
     */
    private fun addPokemonMarkers(pokemonList: List<PokemonResult>) {
        Log.d("MapFragment", "🎯 Añadiendo ${pokemonList.size} marcadores al mapa...")

        // Limpiar marcadores anteriores y el set de capturados
        map.overlays.clear()
        capturedMarkers.clear()

        // Añadir marcadores de Pokémon
        pokemonList.forEach { pokemon ->
            val position = randomGeoPoint(centerPoint)
            addPokemonMarker(pokemon, position)
        }

        // [MEJORA] Añadir marcadores de CURAS (1/3 de la cantidad de Pokémon)
        val cantidadCuras = (pokemonList.size / 3).coerceAtLeast(1)
        Log.d("MapFragment", "💊 Añadiendo $cantidadCuras curas al mapa")

        repeat(cantidadCuras) {
            val position = randomGeoPoint(centerPoint)
            addCuraMarker(position)
        }

        map.invalidate()
        Log.d("MapFragment", "✅ Marcadores añadidos correctamente")
    }

    /**
     * [MEJORA] Añade un marcador de CURA al mapa.
     * Al tocarlo, se añade una cura al inventario.
     */
    private fun addCuraMarker(position: GeoPoint) {
        val marker = Marker(map)
        marker.position = position
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "💊 Poción"

        // Usar un icono diferente para las curas
        try {
            val icon = resources.getDrawable(R.drawable.ic_pokeball_small, null)
            // Teñir el icono de verde para diferenciarlo
            icon.setTint(resources.getColor(R.color.pokemon_grass, null))
            marker.icon = icon
        } catch (e: Exception) {
            Log.w("MapFragment", "No se pudo cargar icono de cura")
        }

        // Al tocar el marcador: RECOGER la cura
        marker.setOnMarkerClickListener { clickedMarker, _ ->
            if (capturedMarkers.contains(clickedMarker)) {
                return@setOnMarkerClickListener true
            }

            Log.d("MapFragment", "💊 Recogiendo cura...")
            capturedMarkers.add(clickedMarker)

            // Añadir cura al inventario
            viewModel.recogerCura()

            // Eliminar el marcador del mapa
            map.overlays.remove(clickedMarker)
            map.invalidate()

            Toast.makeText(requireContext(), "💊 ¡Poción recogida!", Toast.LENGTH_SHORT).show()

            true
        }

        map.overlays.add(marker)
    }

    /**
     * Añade un marcador individual de Pokémon al mapa.
     * Al tocarlo, se captura el Pokémon y se guarda en Room.
     */
    private fun addPokemonMarker(pokemon: PokemonResult, position: GeoPoint) {
        val marker = Marker(map)
        marker.position = position
        marker.title = pokemon.name.replaceFirstChar { it.uppercase() }
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // [MEJORA OPCIONAL] Icono personalizado de Pokébola para los marcadores
        try {
            val icon = androidx.core.content.ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_pokemon_marker
            )
            marker.icon = icon
        } catch (e: Exception) {
            // Si falla, usa el marcador por defecto
            Log.w("MapFragment", "No se pudo cargar icono personalizado")
        }

        // Al tocar el marcador: CAPTURAR el Pokémon
        marker.setOnMarkerClickListener { clickedMarker, _ ->
            // [CORRECCIÓN] Verificar si ya fue capturado
            if (capturedMarkers.contains(clickedMarker)) {
                Log.d("MapFragment", "⚠️ Este Pokémon ya fue capturado")
                return@setOnMarkerClickListener true
            }

            Log.d("MapFragment", "🎯 Intentando capturar: ${clickedMarker.title}")

            // Marcar como capturado inmediatamente para evitar doble clic
            capturedMarkers.add(clickedMarker)

            // Guardar en Room a través del ViewModel
            viewModel.capturarPokemon(
                pokemon,
                clickedMarker.position.latitude,
                clickedMarker.position.longitude
            )

            // [MEJORA] Eliminar el marcador INMEDIATAMENTE del mapa
            map.overlays.remove(clickedMarker)
            map.invalidate()
            Log.d("MapFragment", "✅ Marcador eliminado del mapa")

            // Actualizar contador de Pokémon disponibles
            val remaining = map.overlays.count { it is Marker }
            binding.tvNearbyCount.text = "$remaining Pokémon cercanos"

            true
        }

        map.overlays.add(marker)
    }

    /**
     * Genera un punto geográfico aleatorio cerca del centro.
     */
    private fun randomGeoPoint(center: GeoPoint): GeoPoint {
        val offset = 0.005
        val lat = center.latitude + Random.nextDouble(-offset, offset)
        val lon = center.longitude + Random.nextDouble(-offset, offset)
        return GeoPoint(lat, lon)
    }

    /**
     * Centra el mapa en la ubicación del usuario.
     */
    private fun centrarEnUbicacion() {
        Log.d("MapFragment", "📍 Centrando en ubicación...")
        map.controller.animateTo(centerPoint)
        map.controller.setZoom(16.0)
    }

    /**
     * Navega al fragment de favoritos/Pokédex.
     */
    private fun navegarAFavoritos() {
        Log.d("MapFragment", "⭐ Abriendo Pokédex...")
        findNavController().navigate(R.id.action_map_to_favorites)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // [MEJORA] Detener actualizaciones de ubicación
        if (::locationHelper.isInitialized) {
            locationHelper.detenerActualizaciones()
        }
        _binding = null
    }
}

