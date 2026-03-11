package com.example.monsterapp.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.monsterapp.R
import com.example.monsterapp.data.db.FavoritePokemon
import com.example.monsterapp.databinding.ItemPokemonBinding
import java.util.Locale

class FavoritesAdapter(
    private val onItemClick: (FavoritePokemon) -> Unit,
    private val onRemoveClick: (FavoritePokemon) -> Unit
) : ListAdapter<FavoritePokemon, FavoritesAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {

    inner class FavoriteViewHolder(private val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: FavoritePokemon) {
            binding.tvPokemonName.text = pokemon.name
            binding.tvPokemonId.text = "#${String.format("%03d", pokemon.id)}"
            binding.ivPokemonImage.load(pokemon.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_pokeball)
                error(R.drawable.ic_pokeball)
            }
            binding.root.setOnClickListener { onItemClick(pokemon) }
            binding.root.setOnLongClickListener {
                onRemoveClick(pokemon)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemPokemonBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoritePokemon>() {
            override fun areItemsTheSame(oldItem: FavoritePokemon, newItem: FavoritePokemon) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FavoritePokemon, newItem: FavoritePokemon) =
                oldItem == newItem
        }
    }
}
