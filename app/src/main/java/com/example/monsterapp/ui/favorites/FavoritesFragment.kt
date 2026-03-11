package com.example.monsterapp.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.monsterapp.databinding.FragmentFavoritesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(
            onItemClick = { pokemon ->
                val action = FavoritesFragmentDirections.actionFavoritesToDetail(pokemon.id)
                findNavController().navigate(action)
            },
            onRemoveClick = { pokemon ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Remove Favorite")
                    .setMessage("Remove ${pokemon.name} from favorites?")
                    .setPositiveButton("Remove") { _, _ ->
                        viewModel.removeFavorite(pokemon)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFavorites.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.submitList(favorites)
            if (favorites.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
