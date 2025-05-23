package com.example.safeaid.screens.quiz.viewholder

import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.androidtraining.databinding.QuizItemBinding
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick

class QuizViewHolder(view: View, private val callBack: (Quizze) -> Unit) :
    RecyclerViewHolder<Quizze>(view) {
    private val viewBinding = QuizItemBinding.bind(view)

    override fun bind(position: Int, item: Quizze) {
        viewBinding.tvTitle.text = item.title
        viewBinding.tvDescription.text = item.description
        viewBinding.tvTime.text = formatSecondsToTime(item.duration)

        item.thumbnailUrl?.let { url ->
            Log.i("hihihi", "${url}")
            Glide.with(viewBinding.thumbnail.context)
                .load(url)
                .into(viewBinding.thumbnail)
        }

        viewBinding.cv.setOnDebounceClick {
            callBack.invoke(item)
        }
    }

    private fun formatSecondsToTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }
}