package com.example.safeaid.screens.quiz.viewholder

import android.view.View
import androidx.core.view.isInvisible
import com.example.androidtraining.R
import com.example.androidtraining.databinding.LayoutQuestionItemBinding
import com.example.safeaid.core.response.Question
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick

class QuestionViewHolder(view: View, private val callBack: (Question) -> Unit) :
    RecyclerViewHolder<Question>(view) {
    private val viewBinding = LayoutQuestionItemBinding.bind(view)

    override fun bind(position: Int, item: Question) {
        viewBinding.tv.text = position.toString()

        if (item.isSelected) {
            viewBinding.tv.setBackgroundResource(R.drawable.bg_question)
            viewBinding.line.setBackgroundResource(R.color.primary)
        } else {
            if (item.isAnswered()) {
                viewBinding.tv.setBackgroundResource(R.drawable.bg_answer_selected)
                viewBinding.line.setBackgroundResource(R.color.question_normal)
            } else {
                viewBinding.tv.setBackgroundResource(R.drawable.bg_answer_normal)
                viewBinding.line.setBackgroundResource(R.color.question_normal)
            }

        }

        viewBinding.icFlag.isInvisible = !item.isFlag

        viewBinding.root.setOnDebounceClick {
            callBack.invoke(item)
        }
    }
}