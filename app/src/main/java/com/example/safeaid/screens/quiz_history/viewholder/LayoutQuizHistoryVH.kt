package com.example.safeaid.screens.quiz_history.viewholder

import android.view.View
import com.bumptech.glide.Glide
import com.example.androidtraining.databinding.LayoutQuizHistoryItemBinding
import com.example.androidtraining.databinding.QuizHistoryItemBinding
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz_history.data.QuizHistory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class LayoutQuizHistoryVH(view: View, private val callBack: ((QuizAttempt) -> Unit)?) :
    RecyclerViewHolder<QuizHistory>(view) {
    private val viewBinding = LayoutQuizHistoryItemBinding.bind(view)

    override fun bind(position: Int, item: QuizHistory) {
        viewBinding.tvTitle.text = "${item.quizAttempt.quiz?.title} - ${item.category?.name}"
        viewBinding.tvTime.text = formatSecondsToTime(item.quizAttempt.duration ?: 0)
        viewBinding.tvResult.text = "${item.quizAttempt.score} / ${item.quizAttempt.maxScore}"
        viewBinding.tvDate.text =
            "Ngày kiểm tra: ${convertIsoToDateLegacy(item.quizAttempt.completedAt)}"

        item.quizAttempt.quiz?.thumbnailUrl?.let { url ->
            Glide.with(viewBinding.root.context)
                .load(url)
                .into(viewBinding.thumbnail)
        }

        viewBinding.cv.setOnDebounceClick {
            callBack?.invoke(item.quizAttempt)
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