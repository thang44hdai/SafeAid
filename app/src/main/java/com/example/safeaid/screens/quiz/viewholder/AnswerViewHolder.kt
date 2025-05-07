package com.example.safeaid.screens.quiz.viewholder

import android.graphics.Typeface
import android.view.View
import com.example.androidtraining.R
import com.example.androidtraining.databinding.AnswerItemBinding
import com.example.safeaid.core.response.Answer
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick

class AnswerViewHolder(view: View, private val callBack: (Answer) -> Unit) :
    RecyclerViewHolder<Answer>(view) {
    private val viewBinding = AnswerItemBinding.bind(view)

    override fun bind(position: Int, item: Answer) {
        viewBinding.tvLabel.text = ('A' + position).toString()

        viewBinding.tvValue.text = item.content
        if (item.isSelected) {
            viewBinding.tvLabel.setBackgroundResource(R.drawable.bg_answer_selected)
            viewBinding.tvValue.setTypeface(null, Typeface.BOLD)
        } else {
            viewBinding.tvLabel.setBackgroundResource(R.drawable.bg_answer_normal)
            viewBinding.tvValue.setTypeface(null, Typeface.NORMAL)
        }

        viewBinding.root.setOnDebounceClick {
            callBack.invoke(item)
        }
    }
}