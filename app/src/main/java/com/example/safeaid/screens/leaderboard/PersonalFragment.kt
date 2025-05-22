package com.example.safeaid.screens.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentPersonalBinding
import com.example.safeaid.core.response.PersonalRankResponse
import com.example.safeaid.screens.leaderboard.viewmodel.LeaderboardViewModel
import com.example.safeaid.screens.leaderboard.viewmodel.PersonalRankState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalFragment : Fragment() {
    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LeaderboardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        viewModel.loadPersonalRank()
    }

    private fun observeViewModel() {
        viewModel.personalRankData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PersonalRankState.Loading -> {
                    showLoading(true)
                }
                is PersonalRankState.Success -> {
                    showLoading(false)
                    updateUI(state.data)
                }
                is PersonalRankState.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is PersonalRankState.NoData -> {
                    showLoading(false)
                    showNoDataMessage()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.contentLayout?.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showNoDataMessage() {
        binding.textNoData?.visibility = View.VISIBLE
        binding.contentLayout?.visibility = View.GONE
    }

    private fun updateUI(data: PersonalRankResponse) {
        // Update user info
        binding.textUsername.text = data.user.username

        if (data.user.avatar != null) {
            Glide.with(requireContext())
                .load(data.user.avatar)
                .circleCrop()
                .placeholder(R.drawable.default_avt)
                .into(binding.imageAvatar)
        } else {
            binding.imageAvatar.setImageResource(R.drawable.default_avt)
        }

        // Update stats
        val stats = data.stats
        binding.textScore.text = stats.total_score.toString()
        binding.textRank.text = "#${stats.rank}"
        binding.textAccuracy.text = "${stats.overall_accuracy}%"
        binding.textProgress.text = "${stats.progress.percentage}%"

        // You can also update the category/quiz progress if you have those views
        // For example:
        // binding.progressBar1.progress = stats.progress.percentage

        // If you want to show the quizzes list, you'd need to add a RecyclerView
        // and adapter for that, but that's beyond the current layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}