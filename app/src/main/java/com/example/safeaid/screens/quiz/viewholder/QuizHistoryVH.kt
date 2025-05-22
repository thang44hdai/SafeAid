package com.example.safeaid.screens.quiz.viewholder

import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.androidtraining.databinding.QuizHistoryItemBinding
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class QuizHistoryVH(view: View, private val callBack: ((Quizze) -> Unit)?) :
    RecyclerViewHolder<QuizAttempt>(view) {
    private val viewBinding = QuizHistoryItemBinding.bind(view)

    override fun bind(position: Int, item: QuizAttempt) {
        viewBinding.tvTitle.text = "${item.quiz?.title}"
        viewBinding.tvTime.text =
            "${formatSecondsToTime(item.duration ?: 0)} - ${convertIsoToDateLegacy(item.completedAt)}"
        viewBinding.tvResult.text = "${item.score} / ${item.maxScore}"

        item.quiz?.thumbnailUrl?.let { url ->
            Log.i("hihihi", "${url}")

            Glide.with(viewBinding.thumbnail.context)
                .load(url)
                .into(viewBinding.thumbnail)
        }

        viewBinding.cv.setOnDebounceClick {
            item.quiz?.let { callBack?.invoke(it) }
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

    private fun convertIsoToDateLegacy(input: String?): String {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = isoFormat.parse(input)

        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return outputFormat.format(date!!)
    }

}