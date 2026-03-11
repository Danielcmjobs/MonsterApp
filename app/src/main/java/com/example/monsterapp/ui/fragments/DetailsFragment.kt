package com.example.monsterapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.monsterapp.MonsterExplorerApp
import com.example.monsterapp.R
import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.data.model.PokemonDetail
import com.example.monsterapp.databinding.FragmentDetailsBinding
import com.example.monsterapp.ui.viewmodel.DetailsViewModel
import com.example.monsterapp.ui.viewmodel.PokemonViewModelFactory
import com.example.monsterapp.utils.PokemonTypeColors
import java.util.Locale

/**
 * Fragment de detalle del Pokémon.
 * Muestra toda la información: imagen, nombre, tipos, estadísticas.
 * Integra PokemonTypeColors para mostrar los tipos con sus colores correspondientes.
 * [MEJORA] También permite iniciar un combate contra el Pokémon
 */
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModels {
        val app = requireActivity().application as MonsterExplorerApp
        PokemonViewModelFactory(app.repository)
    }

    private var pokemonId: Int = 0

    // [MEJORA] Variables para almacenar el Pokémon actual (para iniciar combate)
    private var pokemonDetailActual: PokemonDetail? = null
    private var pokemonEntityActual: PokemonEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener el ID del Pokémon desde los argumentos
        pokemonId = arguments?.getInt("pokemonId", 0) ?: 0
        Log.d("DetailsFragment", "📋 Abriendo detalles del Pokémon #$pokemonId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarBotones()
        observarViewModel()

        // Cargar los detalles del Pokémon
        if (pokemonId > 0) {
            viewModel.loadPokemonDetail(pokemonId)
        } else {
            Toast.makeText(requireContext(), "ID de Pokémon inválido", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun configurarBotones() {
        binding.btnBackToMap.setOnClickListener {
            Log.d("DetailsFragment", "🗺️ Volviendo al mapa...")
            findNavController().navigate(R.id.action_detail_to_map)
        }

        // [MEJORA] Botón de combate
        binding.btnBattle.setOnClickListener {
            // Verificar si el Pokémon está herido
            if (pokemonEntityActual?.estaHerido == true) {
                Toast.makeText(requireContext(), "🤕 Este Pokémon está herido. ¡Cúralo primero!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pokemonDetailActual != null && pokemonEntityActual != null) {
                iniciarCombate()
            } else {
                Toast.makeText(requireContext(), "Cargando detalles...", Toast.LENGTH_SHORT).show()
            }
        }

        // [MEJORA] Botón de curar
        binding.btnCurar.setOnClickListener {
            curarPokemon()
        }
    }

    /**
     * [MEJORA] Inicia un combate contra un Pokémon enemigo generado aleatoriamente
     */
    private fun iniciarCombate() {
        try {
            Log.d("DetailsFragment", "⚔️ Iniciando combate contra un Pokémon salvaje...")

            if (pokemonDetailActual == null || pokemonEntityActual == null) {
                Toast.makeText(requireContext(), "Cargando datos del Pokémon...", Toast.LENGTH_SHORT).show()
                Log.e("DetailsFragment", "❌ pokemonDetailActual o pokemonEntityActual es null")
                return
            }

            val miNivel = pokemonEntityActual!!.nivel
            val miPokemonId = pokemonEntityActual!!.pokemonId

            // [MEJORA] El rival tiene un nivel aleatorio entre -10 y +15 del tuyo
            val variacionNivel = (-10..15).random()
            val nivelRival = (miNivel + variacionNivel).coerceIn(1, 100)

            // [MEJORA] El rival es un Pokémon DIFERENTE aleatorio (1-150)
            // Aseguramos que no sea el mismo que el tuyo
            var idRival: Int
            do {
                idRival = (1..150).random()
            } while (idRival == miPokemonId)

            // Lista de nombres para mostrar mientras carga
            val nombresComunes = listOf(
                "Pokémon Salvaje", "Pokémon Misterioso", "Pokémon Feroz",
                "Pokémon Agresivo", "Pokémon Desafiante"
            )
            val nombreTemporal = nombresComunes.random()

            Log.d("DetailsFragment", "📋 Tu Pokémon: #$miPokemonId Lv.$miNivel | Rival: #$idRival Lv.$nivelRival")

            // Navegar al BattleFragment con los IDs
            val bundle = Bundle().apply {
                putInt("playerPokemonId", miPokemonId)
                putInt("playerLevel", miNivel)
                putString("playerName", pokemonEntityActual!!.nombre)
                putInt("opponentPokemonId", idRival)
                putInt("opponentLevel", nivelRival)
                putString("opponentName", nombreTemporal)  // Se actualizará con el nombre real
            }

            Log.d("DetailsFragment", "🚀 Navegando a BattleFragment...")
            findNavController().navigate(R.id.action_detail_to_battle, bundle)
        } catch (e: Exception) {
            Log.e("DetailsFragment", "❌ Error al iniciar combate: ${e.message}", e)
            Toast.makeText(requireContext(), "Error al iniciar combate: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun observarViewModel() {
        // Observar estado de carga
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observar detalles del Pokémon
        viewModel.pokemonDetail.observe(viewLifecycleOwner) { detail ->
            mostrarDetalles(detail)
        }

        // [MEJORA] Observar entidad del Pokémon desde Room (con nivel real)
        viewModel.pokemonEntity.observe(viewLifecycleOwner) { entity ->
            if (entity != null) {
                // Usar el Pokémon con su nivel real de captura
                pokemonEntityActual = entity
                Log.d("DetailsFragment", "📦 Pokémon cargado desde Room: ${entity.nombre} Lv.${entity.nivel}, Herido: ${entity.estaHerido}")

                // [MEJORA] Actualizar UI según estado del Pokémon
                actualizarEstadoUI(entity)
            } else if (pokemonDetailActual != null) {
                // Si no está en Room, crear uno con nivel aleatorio
                pokemonEntityActual = PokemonEntity(
                    pokemonId = pokemonDetailActual!!.id,
                    nombre = pokemonDetailActual!!.name,
                    nivel = (10..50).random(),
                    fechaCaptura = System.currentTimeMillis(),
                    latitud = 0.0,
                    longitud = 0.0
                )
                Log.d("DetailsFragment", "🎲 Pokémon creado con nivel aleatorio: ${pokemonEntityActual!!.nivel}")
                actualizarEstadoUI(pokemonEntityActual!!)
            }
        }

        // [MEJORA] Observar cantidad de curas para habilitar/deshabilitar botón
        viewModel.cantidadCuras.observe(viewLifecycleOwner) { curas ->
            val cantidad = curas ?: 0
            // Actualizar texto y estado del botón curar
            if (binding.btnCurar.visibility == View.VISIBLE) {
                if (cantidad > 0) {
                    binding.btnCurar.text = "💊 Curar ($cantidad)"
                    binding.btnCurar.isEnabled = true
                    binding.btnCurar.alpha = 1.0f
                } else {
                    binding.btnCurar.text = "❌ Sin pociones"
                    binding.btnCurar.isEnabled = false
                    binding.btnCurar.alpha = 0.5f
                }
            }
        }

        // Observar errores
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun mostrarDetalles(pokemon: PokemonDetail) {
        Log.d("DetailsFragment", "✅ Mostrando detalles de ${pokemon.name}")

        // [MEJORA] Guardar el Pokémon actual para usar en combate
        pokemonDetailActual = pokemon

        // Cargar el Pokémon desde Room para obtener el nivel real
        if (pokemonId > 0) {
            // Intentar cargar desde Room (si fue capturado)
            viewModel.cargarPokemonDesdeRoom(pokemonId)
        }

        // Nombre con primera letra en mayúscula
        binding.tvPokemonName.text = pokemon.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        // Número
        binding.tvPokemonNumber.text = String.format(Locale.getDefault(), "#%03d", pokemon.id)

        // Imagen oficial de alta calidad
        val imageUrl = pokemon.sprites.other?.officialArtwork?.frontDefault
            ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png"

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_pokemon_placeholder)
            .error(R.drawable.ic_pokemon_placeholder)
            .into(binding.ivPokemonImage)

        // Tipos (usando PokemonTypeColors)
        mostrarTipos(pokemon)

        // Estadísticas
        mostrarEstadisticas(pokemon)
    }

    /**
     * Muestra los tipos del Pokémon con sus colores correspondientes.
     * Integra PokemonTypeColors para aplicar los colores correctos.
     */
    private fun mostrarTipos(pokemon: PokemonDetail) {
        if (pokemon.types.isNotEmpty()) {
            val tipo1 = pokemon.types[0].type.name
            binding.chipType1.apply {
                text = traducirTipo(tipo1)
                visibility = View.VISIBLE
                // [MEJORA] Integrar colores por tipo
                val colorRes = PokemonTypeColors.getColorForType(tipo1)
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), colorRes)
            }
        }

        if (pokemon.types.size > 1) {
            val tipo2 = pokemon.types[1].type.name
            binding.chipType2.apply {
                text = traducirTipo(tipo2)
                visibility = View.VISIBLE
                // [MEJORA] Integrar colores por tipo
                val colorRes = PokemonTypeColors.getColorForType(tipo2)
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), colorRes)
            }
        } else {
            binding.chipType2.visibility = View.GONE
        }
    }

    /**
     * Muestra las estadísticas del Pokémon.
     */
    private fun mostrarEstadisticas(pokemon: PokemonDetail) {
        pokemon.stats.forEach { stat ->
            when (stat.stat.name) {
                "hp" -> {
                    binding.statHp.tvStatName.text = "HP"
                    binding.statHp.tvStatValue.text = stat.baseStat.toString()
                    binding.statHp.progressStat.progress = stat.baseStat
                }
                "attack" -> {
                    binding.statAttack.tvStatName.text = "ATK"
                    binding.statAttack.tvStatValue.text = stat.baseStat.toString()
                    binding.statAttack.progressStat.progress = stat.baseStat
                }
                "defense" -> {
                    binding.statDefense.tvStatName.text = "DEF"
                    binding.statDefense.tvStatValue.text = stat.baseStat.toString()
                    binding.statDefense.progressStat.progress = stat.baseStat
                }
                "speed" -> {
                    binding.statSpeed.tvStatName.text = "VEL"
                    binding.statSpeed.tvStatValue.text = stat.baseStat.toString()
                    binding.statSpeed.progressStat.progress = stat.baseStat
                }
            }
        }
    }

    /**
     * Traduce los nombres de tipos del inglés al español.
     */
    private fun traducirTipo(tipo: String): String {
        return when (tipo.lowercase()) {
            "normal" -> "Normal"
            "fire" -> "Fuego"
            "water" -> "Agua"
            "electric" -> "Eléctrico"
            "grass" -> "Planta"
            "ice" -> "Hielo"
            "fighting" -> "Lucha"
            "poison" -> "Veneno"
            "ground" -> "Tierra"
            "flying" -> "Volador"
            "psychic" -> "Psíquico"
            "bug" -> "Bicho"
            "rock" -> "Roca"
            "ghost" -> "Fantasma"
            "dragon" -> "Dragón"
            "dark" -> "Siniestro"
            "steel" -> "Acero"
            "fairy" -> "Hada"
            else -> tipo.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * [MEJORA] Actualiza la UI según el estado del Pokémon (herido o sano).
     */
    private fun actualizarEstadoUI(pokemon: PokemonEntity) {
        if (pokemon.estaHerido) {
            // Pokémon HERIDO
            binding.tvEstadoPokemon.visibility = View.VISIBLE
            binding.tvEstadoPokemon.text = "🤕 HERIDO"
            binding.tvEstadoPokemon.setTextColor(resources.getColor(R.color.pokemon_red, null))

            // Mostrar botón curar, ocultar botón combatir
            binding.btnCurar.visibility = View.VISIBLE
            binding.btnBattle.visibility = View.GONE

            Log.d("DetailsFragment", "🤕 Pokémon herido - Mostrando botón curar")
        } else {
            // Pokémon SANO
            binding.tvEstadoPokemon.visibility = View.GONE

            // Mostrar botón combatir, ocultar botón curar
            binding.btnCurar.visibility = View.GONE
            binding.btnBattle.visibility = View.VISIBLE

            Log.d("DetailsFragment", "✅ Pokémon sano - Mostrando botón combatir")
        }
    }

    /**
     * [MEJORA] Cura el Pokémon usando una poción del inventario.
     */
    private fun curarPokemon() {
        val pokemon = pokemonEntityActual ?: return

        viewModel.curarPokemon(pokemon.pokemonId) { exito ->
            if (exito) {
                Toast.makeText(requireContext(), "💊 ¡${pokemon.nombre} ha sido curado!", Toast.LENGTH_SHORT).show()
                // Recargar el Pokémon para actualizar la UI
                viewModel.cargarPokemonDesdeRoom(pokemon.pokemonId)
            } else {
                Toast.makeText(requireContext(), "❌ No tienes pociones. ¡Recoge más en el mapa!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

