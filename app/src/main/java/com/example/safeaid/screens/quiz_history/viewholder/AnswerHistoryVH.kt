package com.example.safeaid.screens.quiz_history.viewholder

import android.graphics.Typeface
import android.view.View
import com.example.androidtraining.R
import com.example.androidtraining.databinding.AnswerItemBinding
import com.example.safeaid.core.response.Answer
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder

class AnswerHistoryVH(view: View, private val getSelectedAnswerId: (item: Answer) -> String?) :
    RecyclerViewHolder<Answer>(view) {
    private val viewBinding = AnswerItemBinding.bind(view)


    override fun bind(position: Int, item: Answer) {
        viewBinding.tvLabel.text = ('A' + position).toString()

        viewBinding.tvValue.text = item.content

        val selectedAnswerId = getSelectedAnswerId.invoke(item)

        if (item.answerId == selectedAnswerId) {
            viewBinding.tvLabel.setBackgroundResource(R.drawable.bg_question)
        }

        if (item.isCorrect == 1) {
            viewBinding.tvLabel.setBackgroundResource(R.drawable.bg_question_correct)
        }
    }
}