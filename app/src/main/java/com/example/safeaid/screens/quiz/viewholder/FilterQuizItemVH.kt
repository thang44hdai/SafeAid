package com.example.safeaid.screens.quiz.viewholder

import android.view.View
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.LayoutQuizFilterItemBinding
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.data.QuizFilterItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class FilterQuizItemVH(view: View, private val callBack: (QuizFilterItem) -> Unit) :
    RecyclerViewHolder<QuizFilterItem>(view) {
    private val viewBinding = LayoutQuizFilterItemBinding.bind(view)

    override fun bind(position: Int, item: QuizFilterItem) {
        viewBinding.tvTitle.text = item.quiz.title
        viewBinding.tvDescription.text = item.quiz.description
        viewBinding.tvTime.text = formatSecondsToTime(item.quiz.duration)
        if (item.isDone) {
            viewBinding.icStatus.setBackgroundResource(R.drawable.ic_checked_box)
            viewBinding.tvStatus.text = "Đã làm"
        } else {
            viewBinding.icStatus.setBackgroundResource(R.drawable.ic_check_box)
            viewBinding.tvStatus.text = "Chưa làm"
        }

        item.quiz.thumbnailUrl?.let { url ->
            Glide.with(viewBinding.root.context)
                .load(url)
                .into(viewBinding.thumbnail) // Set the image into the ImageView
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

    private fun convertIsoToDateLegacy(input: String?): String {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = isoFormat.parse(input)

        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return outputFormat.format(date!!)
    }

}