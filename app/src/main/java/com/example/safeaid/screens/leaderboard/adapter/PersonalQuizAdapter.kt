package com.example.safeaid.screens.leaderboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtraining.databinding.ItemPersonalQuizBinding
import com.example.safeaid.core.response.QuizAttempt

class PersonalQuizAdapter : RecyclerView.Adapter<PersonalQuizAdapter.ViewHolder>() {
    private var quizAttempts = listOf<QuizAttempt>()

    fun setData(data: List<QuizAttempt>) {
        // Group attempts by quiz_id and keep only the one with highest score
        quizAttempts = data
            .groupBy { it.quizId } // Group by quiz_id
            .mapValues { (_, attempts) -> 
                attempts.maxByOrNull { attempt -> 
                    attempt.score ?: 0 
                } // Get attempt with highest score
            }
            .mapNotNull { it.value } // Convert to list and remove nulls
            .sortedByDescending { it.completedAt } // Sort by completion time, newest first
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPersonalQuizBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(quizAttempts[position])
    }

    override fun getItemCount() = quizAttempts.size

    class ViewHolder(private val binding: ItemPersonalQuizBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quizAttempt: QuizAttempt) {
            binding.textTitle.text = quizAttempt.quiz?.title ?: ""
            binding.textScore.text = "${quizAttempt.score ?: 0}/${quizAttempt.maxScore ?: 0}"
            
            val score = quizAttempt.score ?: 0
            val maxScore = quizAttempt.maxScore ?: 0
            val progress = if (maxScore > 0) {
                (score * 100) / maxScore
            } else {
                0
            }
            binding.progressBar.progress = progress
        }
    }
} 