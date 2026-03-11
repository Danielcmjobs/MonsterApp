package com.example.monsterapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.monsterapp.MonsterExplorerApp
import com.example.monsterapp.R
import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.databinding.FragmentBattleBinding
import com.example.monsterapp.ui.viewmodel.BattleViewModel
import com.example.monsterapp.ui.viewmodel.PokemonViewModelFactory

/**
 * [MEJORA] Fragment para la pantalla de combate
 * Muestra la batalla en tiempo real entre dos Pokémon
 */
class BattleFragment : Fragment() {

    private var _binding: FragmentBattleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BattleViewModel by viewModels {
        val app = requireActivity().application as MonsterExplorerApp
        PokemonViewModelFactory(app.repository)
    }

    private var playerPokemonId: Int = 0
    private var playerLevel: Int = 50
    private var playerName: String = "Tu Pokémon"
    private var opponentPokemonId: Int = 0
    private var opponentLevel: Int = 50
    private var opponentName: String = "Pokémon Rival"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBattleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Obtener argumentos
            arguments?.let { args ->
                playerPokemonId = args.getInt("playerPokemonId", 1)
                playerLevel = args.getInt("playerLevel", 50)
                playerName = args.getString("playerName", "Tu Pokémon") ?: "Tu Pokémon"
                opponentPokemonId = args.getInt("opponentPokemonId", 1)
                opponentLevel = args.getInt("opponentLevel", 50)
                opponentName = args.getString("opponentName", "Pokémon Rival") ?: "Pokémon Rival"
            }

            Log.d("BattleFragment", "⚔️ Iniciando batalla: $playerName (Lv.$playerLevel) vs $opponentName (Lv.$opponentLevel)")

            // Crear entidades ficticias para el combate
            val playerEntity = PokemonEntity(
                pokemonId = playerPokemonId,
                nombre = playerName,
                nivel = playerLevel,
                fechaCaptura = System.currentTimeMillis(),
                latitud = 0.0,
                longitud = 0.0
            )

            val opponentEntity = PokemonEntity(
                pokemonId = opponentPokemonId,
                nombre = opponentName,
                nivel = opponentLevel,
                fechaCaptura = System.currentTimeMillis(),
                latitud = 0.0,
                longitud = 0.0
            )

            // Configurar UI básica
            binding.tvPlayerName.text = "$playerName Lv. $playerLevel"
            binding.tvOpponentName.text = "$opponentName Lv. $opponentLevel"

            // Cargar imágenes
            cargarImagen(playerPokemonId, binding.ivPlayerImage)
            cargarImagen(opponentPokemonId, binding.ivOpponentImage)

            // Mostrar loading
            binding.progressLoading.visibility = View.VISIBLE

            // Configurar botones
            configurarBotones()

            // Observar cambios del combate ANTES de cargar
            observarBattle()

            // Cargar detalles de la API e iniciar combate
            viewModel.cargarDetallesYCombatir(playerEntity, opponentEntity, playerPokemonId, opponentPokemonId)
        } catch (e: Exception) {
            Log.e("BattleFragment", "❌ Error en onViewCreated: ${e.message}", e)
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
    }

    private fun configurarBotones() {
        binding.btnAttack.setOnClickListener {
            binding.btnAttack.isEnabled = false
            binding.btnSurrender.isEnabled = false
            viewModel.jugadorAtaca()
        }

        binding.btnSurrender.setOnClickListener {
            Toast.makeText(requireContext(), "¡Huiste del combate!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun cargarImagen(pokemonId: Int, imageView: android.widget.ImageView) {
        val imageUrl =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_pokemon_placeholder)
            .error(R.drawable.ic_pokemon_placeholder)
            .into(imageView)
    }

    private fun observarBattle() {
        // Observar errores
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("BattleFragment", "❌ Error: $it")
                binding.progressLoading.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }

        // [MEJORA] Observar el nombre real del oponente cuando se cargue de la API
        viewModel.opponentRealName.observe(viewLifecycleOwner) { nombreReal ->
            if (nombreReal.isNotEmpty()) {
                binding.tvOpponentName.text = "$nombreReal Lv. $opponentLevel"
                Log.d("BattleFragment", "📛 Nombre del rival actualizado: $nombreReal")
            }
        }

        viewModel.battleState.observe(viewLifecycleOwner) { state ->
            Log.d("BattleFragment", "🎮 Estado actualizado - Turno ${state.turno}")

            binding.progressLoading.visibility = View.GONE

            // Actualizar HP del jugador
            val playerHPPercent = (state.playerPokemon.currentHP * 100) / state.playerPokemon.maxHP
            binding.progressPlayerHP.progress = playerHPPercent.coerceIn(0, 100)
            binding.tvPlayerHP.text = "${state.playerPokemon.currentHP}/${state.playerPokemon.maxHP}"

            // Actualizar HP del oponente
            val opponentHPPercent = (state.opponentPokemon.currentHP * 100) / state.opponentPokemon.maxHP
            binding.progressOpponentHP.progress = opponentHPPercent.coerceIn(0, 100)
            binding.tvOpponentHP.text = "${state.opponentPokemon.currentHP}/${state.opponentPokemon.maxHP}"

            // Actualizar mensaje
            binding.tvBattleMessage.text = state.mensajeActual

            // Reactivar botones si el combate continúa
            if (!state.haTerminado()) {
                binding.btnAttack.isEnabled = true
                binding.btnSurrender.isEnabled = true
            }
        }

        viewModel.battleMessage.observe(viewLifecycleOwner) { message ->
            binding.tvBattleMessage.text = message
        }

        viewModel.battleOver.observe(viewLifecycleOwner) { isOver ->
            if (isOver) {
                binding.btnAttack.isEnabled = false
                binding.btnSurrender.isEnabled = false

                // Mostrar resultado después de 2 segundos
                binding.btnAttack.postDelayed({
                    mostrarResultado()
                }, 2000)
            }
        }
    }

    private fun mostrarResultado() {
        val winner = viewModel.winner.value
        val nivelesGanados = viewModel.nivelesGanados.value ?: 0

        if (winner == "PLAYER") {
            val mensaje = if (nivelesGanados > 0) {
                "🎉 ¡GANASTE!\n+$nivelesGanados nivel(es)"
            } else {
                "🎉 ¡GANASTE!"
            }
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "💔 ¡PERDISTE!", Toast.LENGTH_LONG).show()
        }

        // Volver a la pantalla anterior después de 2 segundos
        binding.root.postDelayed({
            findNavController().navigateUp()
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

