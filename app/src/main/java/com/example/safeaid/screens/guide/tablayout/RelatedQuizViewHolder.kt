package com.example.safeaid.screens.guide.tablayout

import android.view.View
import com.example.androidtraining.databinding.ItemRelatedQuizBinding
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder

class RelatedQuizViewHolder(
    view: View,
    private val onQuizClick: (Quizze) -> Unit
) : RecyclerViewHolder<Quizze>(view) {
    private val binding = ItemRelatedQuizBinding.bind(view)

    override fun bind(position: Int, item: Quizze) {
        binding.tvTitle.text = item.title
        binding.root.setOnClickListener { onQuizClick(item) }
    }
}