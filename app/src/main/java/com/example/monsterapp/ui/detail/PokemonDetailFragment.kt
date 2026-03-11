package com.example.monsterapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.monsterapp.R
import com.example.monsterapp.data.model.PokemonDetail
import com.example.monsterapp.databinding.FragmentPokemonDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PokemonDetailViewModel by viewModels()
    private val args: PokemonDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.loadPokemon(args.pokemonId)
        observeViewModel()

        binding.fabFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun observeViewModel() {
        viewModel.pokemonDetail.observe(viewLifecycleOwner) { detail ->
            detail?.let { populateUI(it) }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry) {
                        viewModel.loadPokemon(args.pokemonId)
                    }.show()
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.fabFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )
        }
    }

    private fun populateUI(detail: PokemonDetail) {
        binding.toolbar.title = detail.name.replaceFirstChar { it.titlecase(Locale.ROOT) }

        val imageUrl = detail.sprites.other?.officialArtwork?.frontDefault
            ?: detail.sprites.frontDefault

        binding.ivPokemonArtwork.load(imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_pokeball)
        }

        binding.tvPokemonId.text = "#${String.format("%03d", detail.id)}"
        binding.tvPokemonName.text = detail.name.replaceFirstChar { it.titlecase(Locale.ROOT) }

        // Types
        val typesText = detail.types.joinToString("  •  ") { it.type.name.uppercase(Locale.ROOT) }
        binding.tvTypes.text = typesText

        // Physical data
        binding.tvHeight.text = getString(R.string.height_value, detail.height / 10.0)
        binding.tvWeight.text = getString(R.string.weight_value, detail.weight / 10.0)

        // Base experience
        binding.tvBaseExp.text = detail.baseExperience?.toString() ?: "--"

        // Stats
        detail.stats.forEach { statSlot ->
            val statName = statSlot.stat.name
            val statValue = statSlot.baseStat
            when (statName) {
                "hp" -> {
                    binding.tvHpValue.text = statValue.toString()
                    binding.progressHp.progress = statValue
                }
                "attack" -> {
                    binding.tvAttackValue.text = statValue.toString()
                    binding.progressAttack.progress = statValue
                }
                "defense" -> {
                    binding.tvDefenseValue.text = statValue.toString()
                    binding.progressDefense.progress = statValue
                }
                "special-attack" -> {
                    binding.tvSpAttackValue.text = statValue.toString()
                    binding.progressSpAttack.progress = statValue
                }
                "special-defense" -> {
                    binding.tvSpDefenseValue.text = statValue.toString()
                    binding.progressSpDefense.progress = statValue
                }
                "speed" -> {
                    binding.tvSpeedValue.text = statValue.toString()
                    binding.progressSpeed.progress = statValue
                }
            }
        }

        // Abilities
        val abilitiesText = detail.abilities.joinToString(", ") { ability ->
            ability.ability.name.replace("-", " ")
                .replaceFirstChar { it.titlecase(Locale.ROOT) }
        }
        binding.tvAbilities.text = abilitiesText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
