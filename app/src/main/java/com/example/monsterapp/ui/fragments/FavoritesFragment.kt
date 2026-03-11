package com.example.monsterapp.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monsterapp.MonsterExplorerApp
import com.example.monsterapp.R
import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.databinding.FragmentFavoritesBinding
import com.example.monsterapp.ui.adapters.FavoritesAdapter
import com.example.monsterapp.ui.viewmodel.FavoritosViewModel
import com.example.monsterapp.ui.viewmodel.PokemonViewModelFactory
import com.example.monsterapp.utils.StatsUtils
import java.util.Locale

/**
 * Fragment que muestra la Pokédex con los Pokémon capturados.
 *
 * Funcionalidades:
 * - Lista de Pokémon capturados (desde Room)
 * - Estado vacío cuando no hay capturas
 * - Liberar Pokémon con long press
 * - Actualización automática cuando cambia la BD
 */
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    // ViewModel con Factory para inyectar el repositorio
    private val viewModel: FavoritosViewModel by viewModels {
        val app = requireActivity().application as MonsterExplorerApp
        PokemonViewModelFactory(app.repository)
    }

    // Adapter para el RecyclerView
    private lateinit var adapter: FavoritesAdapter

    // [MEJORA] Lista original para filtrado
    private var listaOriginal: List<PokemonEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FavoritesFragment", "⭐ Pokédex abierta")

        // Configurar RecyclerView
        configurarRecyclerView()

        // Configurar botones
        configurarBotones()

        // Observar datos del ViewModel
        observarViewModel()
    }

    /**
     * Configura el RecyclerView con el adapter.
     */
    private fun configurarRecyclerView() {
        adapter = FavoritesAdapter(
            onItemClick = { pokemon ->
                // Navegar a los detalles del Pokémon capturado
                Log.d("FavoritesFragment", "📋 Navegando a detalles de ${pokemon.nombre}")
                navegarADetalles(pokemon.pokemonId)
            },
            onLongClick = { pokemon ->
                mostrarDialogoLiberar(pokemon)
                true
            }
        )

        binding.rvCapturedPokemon.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritesFragment.adapter
        }
    }

    /**
     * Configura los botones de la UI.
     */
    private fun configurarBotones() {
        // Botón de volver
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // [MEJORA] Configurar SearchView con texto e icono en negro
        configurarSearchView()

        // [MEJORA] Búsqueda en tiempo real
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filtrar lista según el texto de búsqueda
                filtrarPokemon(newText ?: "")
                return true
            }
        })
    }

    /**
     * [MEJORA] Configura el SearchView para que el texto y los iconos sean negros.
     */
    private fun configurarSearchView() {
        try {
            // Obtener el EditText interno del SearchView
            val searchEditText = binding.searchView.findViewById<android.widget.EditText>(
                androidx.appcompat.R.id.search_src_text
            )
            searchEditText?.setTextColor(resources.getColor(R.color.black, null))
            searchEditText?.setHintTextColor(resources.getColor(R.color.text_secondary, null))

            // Obtener el icono de la lupa
            val searchIcon = binding.searchView.findViewById<android.widget.ImageView>(
                androidx.appcompat.R.id.search_mag_icon
            )
            searchIcon?.setColorFilter(resources.getColor(R.color.black, null))

            // Obtener el icono de cerrar (X)
            val closeIcon = binding.searchView.findViewById<android.widget.ImageView>(
                androidx.appcompat.R.id.search_close_btn
            )
            closeIcon?.setColorFilter(resources.getColor(R.color.black, null))

        } catch (e: Exception) {
            Log.w("FavoritesFragment", "No se pudo configurar SearchView: ${e.message}")
        }
    }

    /**
     * Observa los cambios en el ViewModel.
     */
    private fun observarViewModel() {
        // Observar lista de Pokémon
        viewModel.listaPokemon.observe(viewLifecycleOwner) { lista ->
            Log.d("FavoritesFragment", "📋 Recibidos ${lista.size} Pokémon capturados")

            // [MEJORA] Guardar lista original para filtrado
            listaOriginal = lista

            // [MEJORA] Calcular y mostrar estadísticas
            mostrarEstadisticas(lista)

            adapter.submitList(lista)

            if (lista.isEmpty()) {
                mostrarEstadoVacio()
            } else {
                mostrarListaPokemon(lista.size)
            }
        }

        // Observar contador
        viewModel.contadorCapturados.observe(viewLifecycleOwner) { count ->
            binding.tvCapturedCount.text = "$count Pokémon capturados"
        }

        // [MEJORA] Observar cantidad de curas
        viewModel.cantidadCuras.observe(viewLifecycleOwner) { curas ->
            val cantidad = curas ?: 0
            binding.tvCurasCount.text = "💊 $cantidad pociones"
            Log.d("FavoritesFragment", "💊 Curas disponibles: $cantidad")
        }

        // [MEJORA] Observar Pokémon heridos
        viewModel.contadorHeridos.observe(viewLifecycleOwner) { heridos ->
            if (heridos > 0) {
                binding.tvHeridosCount.text = "🤕 $heridos heridos"
                binding.tvHeridosCount.visibility = View.VISIBLE
            } else {
                binding.tvHeridosCount.visibility = View.GONE
            }
        }
    }

    /**
     * Muestra el mensaje de Pokédex vacía.
     */
    private fun mostrarEstadoVacio() {
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.rvCapturedPokemon.visibility = View.GONE
        Log.d("FavoritesFragment", "📭 Pokédex vacía")
    }

    /**
     * Muestra la lista de Pokémon capturados.
     */
    private fun mostrarListaPokemon(count: Int) {
        binding.layoutEmpty.visibility = View.GONE
        binding.rvCapturedPokemon.visibility = View.VISIBLE
        Log.d("FavoritesFragment", "📋 Mostrando $count Pokémon")
    }

    /**
     * Muestra un diálogo de confirmación para liberar un Pokémon.
     * [MEJORA] Si el Pokémon está herido, muestra un mensaje especial culpabilizando al jugador 😈
     */
    private fun mostrarDialogoLiberar(pokemon: PokemonEntity) {
        val nombrePokemon = pokemon.nombre.replaceFirstChar { it.uppercase() }

        if (pokemon.estaHerido) {
            // Diálogo especial para Pokémon herido 😈
            AlertDialog.Builder(requireContext())
                .setTitle("😱 ¿En serio?")
                .setMessage("¿Estás seguro de que quieres liberar a $nombrePokemon estando herido? Tú sí que eres un monstruo...")
                .setPositiveButton("Lo sé, la vida es dura.") { _, _ ->
                    viewModel.liberarPokemon(pokemon)
                    Toast.makeText(
                        requireContext(),
                        "💔 $nombrePokemon se fue cojeando... esperamos que sobreviva.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setNegativeButton("Tienes razón... lo siento") { _, _ ->
                    Toast.makeText(
                        requireContext(),
                        "❤️ $nombrePokemon te lo agradece. ¡Ahora cúralo!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .show()
        } else {
            // Diálogo normal para Pokémon sano
            AlertDialog.Builder(requireContext())
                .setTitle("¿Liberar $nombrePokemon?")
                .setMessage("El Pokémon será eliminado de tu Pokédex.")
                .setPositiveButton("Liberar") { _, _ ->
                    viewModel.liberarPokemon(pokemon)
                    Toast.makeText(
                        requireContext(),
                        "¡$nombrePokemon liberado!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    /**
     * Navega a la pantalla de detalles pasando el ID del Pokémon.
     */
    private fun navegarADetalles(pokemonId: Int) {
        val bundle = Bundle().apply {
            putInt("pokemonId", pokemonId)
        }
        findNavController().navigate(R.id.action_favorites_to_detail, bundle)
    }

    /**
     * [MEJORA] Calcula y muestra las estadísticas de la colección.
     */
    private fun mostrarEstadisticas(pokemonList: List<PokemonEntity>) {
        val stats = StatsUtils.calcularEstadisticas(pokemonList)

        binding.tvAverageLevel.text = String.format(Locale.getDefault(), "%.1f", stats.nivelPromedio)
        binding.tvHighestLevel.text = stats.nivelMasAlto.toString()
        binding.tvStrongestName.text = stats.nombreMasFuerte

        Log.d("FavoritesFragment", "📊 Stats: Promedio=${stats.nivelPromedio}, Máximo=${stats.nivelMasAlto}, Más Fuerte=${stats.nombreMasFuerte}")
    }

    /**
     * [MEJORA] Filtra la lista de Pokémon según el texto de búsqueda.
     */
    private fun filtrarPokemon(searchText: String) {
        val listaFiltrada = if (searchText.isBlank()) {
            listaOriginal
        } else {
            listaOriginal.filter { pokemon ->
                pokemon.nombre.lowercase().contains(searchText.lowercase())
            }
        }

        Log.d("FavoritesFragment", "🔍 Búsqueda: '$searchText' - ${listaFiltrada.size} resultados")

        if (listaFiltrada.isEmpty()) {
            mostrarEstadoVacio()
        } else {
            mostrarListaPokemon(listaFiltrada.size)
        }

        adapter.submitList(listaFiltrada)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

