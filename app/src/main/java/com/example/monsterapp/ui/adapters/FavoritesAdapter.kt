package com.example.monsterapp.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monsterapp.R
import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.databinding.PokemonItemBinding
import com.example.monsterapp.utils.DateUtils
import java.util.Locale

/**
 * Adapter para mostrar la lista de Pokémon capturados en un RecyclerView.
 */
class FavoritesAdapter(
    private val onItemClick: (PokemonEntity) -> Unit,
    private val onLongClick: (PokemonEntity) -> Boolean = { false }
) : ListAdapter<PokemonEntity, FavoritesAdapter.PokemonViewHolder>(PokemonDiffCallback()) {

    class PokemonViewHolder(
        val binding: PokemonItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = PokemonItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = getItem(position)
        val ctx = holder.itemView.context

        with(holder.binding) {
            // Nombre con primera letra en mayúscula
            // [MEJORA] Añadir indicador de herido
            val nombreBase = pokemon.nombre.replaceFirstChar { it.uppercase() }
            tvPokemonName.text = if (pokemon.estaHerido) {
                "🤕 $nombreBase"
            } else {
                nombreBase
            }

            // Número + fecha de captura relativa
            val fechaRelativa = DateUtils.getRelativeTime(pokemon.fechaCaptura)
            tvPokemonNumber.text = String.format(Locale.getDefault(), "#%03d · %s", pokemon.pokemonId, fechaRelativa)

            // URL de la imagen oficial de PokeAPI
            val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.pokemonId}.png"

            // Cargar imagen con Glide
            Glide.with(ctx)
                .load(imageUrl)
                .placeholder(R.drawable.ic_pokemon_placeholder)
                .error(R.drawable.ic_pokemon_placeholder)
                .into(imgPokemon)

            // [MEJORA] Mostrar nivel y estado
            chipType1.text = if (pokemon.estaHerido) {
                "Nv. ${pokemon.nivel} 🤕"
            } else {
                "Nv. ${pokemon.nivel}"
            }

            // [MEJORA] Cambiar color del chip si está herido
            if (pokemon.estaHerido) {
                chipType1.setChipBackgroundColorResource(R.color.pokemon_red)
            } else {
                chipType1.setChipBackgroundColorResource(R.color.pokemon_blue)
            }

            // Click para ver detalles
            root.setOnClickListener { onItemClick(pokemon) }

            // Long click para liberar
            root.setOnLongClickListener { onLongClick(pokemon) }

            // Animación de entrada
            try {
                val animation = AnimationUtils.loadAnimation(ctx, android.R.anim.fade_in)
                holder.itemView.startAnimation(animation)
            } catch (e: Exception) {
                Log.w("FavoritesAdapter", "Animación no aplicada: ${e.message}")
            }
        }
    }

    class PokemonDiffCallback : DiffUtil.ItemCallback<PokemonEntity>() {
        override fun areItemsTheSame(oldItem: PokemonEntity, newItem: PokemonEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PokemonEntity, newItem: PokemonEntity): Boolean {
            return oldItem == newItem
        }
    }
}

