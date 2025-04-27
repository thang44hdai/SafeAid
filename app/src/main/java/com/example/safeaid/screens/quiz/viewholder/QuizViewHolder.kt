package com.example.safeaid.screens.quiz.viewholder

import android.view.View
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
        viewBinding.tvTime.text = item.duration.toString()

        viewBinding.cv.setOnDebounceClick {
            callBack.invoke(item)
        }
    }
}