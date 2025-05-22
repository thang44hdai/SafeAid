package com.example.safeaid.screens.leaderboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ItemLeaderboardEntryBinding
import com.example.safeaid.core.response.LeaderboardEntry

class LeaderboardAdapter : ListAdapter<LeaderboardEntry, LeaderboardAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        holder.bind(entry)
    }

    class ViewHolder(private val binding: ItemLeaderboardEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: LeaderboardEntry) {
            binding.textRank.text = "#${entry.rank}"
            binding.textUsername.text = entry.username
            binding.textScore.text = "${entry.total_score} điểm"

            // Load avatar
            if (entry.avatar != null) {
                Glide.with(binding.root)
                    .load(entry.avatar)
                    .circleCrop()
                    .into(binding.imageAvatar)
            } else {
                binding.imageAvatar.setImageResource(R.drawable.default_avt)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<LeaderboardEntry>() {
        override fun areItemsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry): Boolean {
            return oldItem.user_id == newItem.user_id
        }

        override fun areContentsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry): Boolean {
            return oldItem == newItem
        }
    }
}