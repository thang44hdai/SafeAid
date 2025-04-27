package com.example.safeaid.screens.quiz.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ItemQuizCategoryBinding
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.data.ItemQuizCategory

class QuizCategoryViewHolder(view: View, private val callBack: (ItemQuizCategory) -> Unit) :
    RecyclerViewHolder<ItemQuizCategory>(view) {
    private val viewBinding = ItemQuizCategoryBinding.bind(view)

    override fun bind(position: Int, item: ItemQuizCategory) {
        viewBinding.tvTitle.text = item.quizCategory.name

        if (item.isSelected) {
            viewBinding.mainLayout.setBackgroundResource(R.drawable.bg_category_selected)
            viewBinding.tvTitle.setTextColor(
                ContextCompat.getColor(
                    viewBinding.root.context,
                    R.color.white
                )
            )
        } else {
            viewBinding.mainLayout.setBackgroundResource(R.drawable.bg_category_nomal)
            viewBinding.tvTitle.setTextColor(
                ContextCompat.getColor(
                    viewBinding.root.context,
                    R.color.black
                )
            )
        }

        viewBinding.tvTitle.setOnDebounceClick {
            callBack.invoke(item)
        }
    }

}