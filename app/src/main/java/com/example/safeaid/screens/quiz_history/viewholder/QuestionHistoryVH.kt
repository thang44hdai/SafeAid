package com.example.safeaid.screens.quiz_history.viewholder

import android.util.Log
import android.view.View
import androidx.core.view.isInvisible
import com.example.androidtraining.R
import com.example.androidtraining.databinding.LayoutQuestionItemBinding
import com.example.safeaid.core.response.Question
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick

class QuestionHistoryVH(view: View, private val callBack: (Question) -> Unit) :
    RecyclerViewHolder<Question>(view) {
    private val viewBinding = LayoutQuestionItemBinding.bind(view)

    override fun bind(position: Int, item: Question) {
        viewBinding.tv.text = position.toString()
        viewBinding.icFlag.isInvisible = true
        val isCorrect =
            item.answers.any { it.isCorrect == 1 && it.answerId == item.selectedAnswerId }
        if (isCorrect) {
            viewBinding.tv.setBackgroundResource(R.drawable.bg_question_correct)
        } else {
            viewBinding.tv.setBackgroundResource(R.drawable.bg_question)
        }

        viewBinding.line.isInvisible = true
        viewBinding.icFlag.isInvisible = true

        viewBinding.root.setOnDebounceClick {
            callBack.invoke(item)
        }
    }
}