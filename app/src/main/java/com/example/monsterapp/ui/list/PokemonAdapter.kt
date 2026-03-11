package com.example.monsterapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.monsterapp.R
import com.example.monsterapp.data.model.PokemonEntry
import com.example.monsterapp.databinding.ItemPokemonBinding
import java.util.Locale

class PokemonAdapter(
    private val onItemClick: (PokemonEntry) -> Unit
) : ListAdapter<PokemonEntry, PokemonAdapter.PokemonViewHolder>(DIFF_CALLBACK) {

    inner class PokemonViewHolder(private val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon: PokemonEntry) {
            binding.tvPokemonName.text = pokemon.name.replaceFirstChar {
                it.titlecase(Locale.ROOT)
            }
            binding.tvPokemonId.text = "#${String.format("%03d", pokemon.id)}"
            binding.ivPokemonImage.load(pokemon.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_pokeball)
                error(R.drawable.ic_pokeball)
            }
            binding.root.setOnClickListener { onItemClick(pokemon) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PokemonEntry>() {
            override fun areItemsTheSame(oldItem: PokemonEntry, newItem: PokemonEntry) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PokemonEntry, newItem: PokemonEntry) =
                oldItem == newItem
        }
    }
}
