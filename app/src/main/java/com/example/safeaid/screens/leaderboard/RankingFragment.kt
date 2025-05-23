package com.example.safeaid.screens.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentRankingBinding
import com.example.safeaid.core.response.LeaderboardEntry
import com.example.safeaid.screens.leaderboard.adapter.LeaderboardAdapter
import com.example.safeaid.screens.leaderboard.viewmodel.LeaderboardState
import com.example.safeaid.screens.leaderboard.viewmodel.LeaderboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LeaderboardViewModel by activityViewModels()
    private lateinit var adapter: LeaderboardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadLeaderboard()
    }

    private fun setupRecyclerView() {
        adapter = LeaderboardAdapter()
        binding.rvRankingList.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.leaderboardData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LeaderboardState.Loading -> {
                    // Show loading indicator
                    showLoading(true)
                }
                is LeaderboardState.Success -> {
                    // Hide loading, show data
                    showLoading(false)

                    // Update the Top 3 UI
                    updateTop3(state.entries)

                    // Update the remaining list (positions 4+)
                    val remainingEntries = if (state.entries.size > 3) {
                        state.entries.subList(3, state.entries.size)
                    } else {
                        emptyList()
                    }
                    adapter.submitList(remainingEntries)
                }
                is LeaderboardState.Error -> {
                    // Hide loading, show error
                    showLoading(false)
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTop3(entries: List<LeaderboardEntry>) {
        // First place
        if (entries.isNotEmpty()) {
            updateTopUser(entries[0], 1)

            // Remove entries after position 3 for the regular list
            val remainingEntries = if (entries.size > 3) {
                entries.subList(3, entries.size)
            } else {
                emptyList()
            }

            adapter.submitList(remainingEntries)
        }

        // Second place
        if (entries.size > 1) {
            updateTopUser(entries[1], 2)
        }

        // Third place
        if (entries.size > 2) {
            updateTopUser(entries[2], 3)
        }
    }

    private fun updateTopUser(entry: LeaderboardEntry, position: Int) {
        when (position) {
            1 -> {
                // Update first place (middle card)
                binding.textUsername1.text = entry.username
                binding.textScore1.text = entry.total_score.toString()

                if (entry.avatar != null) {
                    Glide.with(requireContext())
                        .load(entry.avatar)
                        .circleCrop()
                        .placeholder(R.drawable.default_avt)
                        .into(binding.imageAvatar1)
                } else {
                    binding.imageAvatar1.setImageResource(R.drawable.default_avt)
                }
            }
            2 -> {
                // Update second place (left card)
                binding.textUsername2.text = entry.username
                binding.textScore2.text = entry.total_score.toString()

                if (entry.avatar != null) {
                    Glide.with(requireContext())
                        .load(entry.avatar)
                        .circleCrop()
                        .placeholder(R.drawable.default_avt)
                        .into(binding.imageAvatar2)
                } else {
                    binding.imageAvatar2.setImageResource(R.drawable.default_avt)
                }
            }
            3 -> {
                // Update third place (right card)
                binding.textUsername3.text = entry.username
                binding.textScore3.text = entry.total_score.toString()

                if (entry.avatar != null) {
                    Glide.with(requireContext())
                        .load(entry.avatar)
                        .circleCrop()
                        .placeholder(R.drawable.default_avt)
                        .into(binding.imageAvatar3)
                } else {
                    binding.imageAvatar3.setImageResource(R.drawable.default_avt)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar?.visibility = View.VISIBLE
            binding.rvRankingList.visibility = View.GONE
        } else {
            binding.progressBar?.visibility = View.GONE
            binding.rvRankingList.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}