package com.example.safeaid.screens.bookmark.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtraining.databinding.ItemBookmarkBinding
import com.example.safeaid.screens.guide.GuideItem

class BookmarkViewHolder(
    private val binding: ItemBookmarkBinding,
    private val onItemClick: (GuideItem) -> Unit,
    private val onItemLongClick: () -> Unit,
    private val onItemSelected: (String, Boolean) -> Unit,
    private val isSelectionMode: () -> Boolean,
    private val selectedItems: () -> Set<String>,
    private val getItem: (Int) -> GuideItem
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (isSelectionMode()) {
                    val item = getItem(position)
                    if (selectedItems().contains(item.id)) {
                        binding.checkbox.isChecked = false
                        onItemSelected(item.id, false)
                    } else {
                        binding.checkbox.isChecked = true
                        onItemSelected(item.id, true)
                    }
                } else {
                    onItemClick(getItem(position))
                }
            }
        }

        binding.root.setOnLongClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION && !isSelectionMode()) {
                onItemLongClick()
                val item = getItem(position)
                binding.checkbox.isChecked = true
                onItemSelected(item.id, true)
            }
            true
        }
    }

    fun bind(item: GuideItem) {
        binding.apply {
            tvTitle.text = item.title
            tvDescription.text = item.description
            checkbox.visibility = if (isSelectionMode()) View.VISIBLE else View.GONE
            btnNext.visibility = if (isSelectionMode()) View.GONE else View.VISIBLE
            checkbox.isChecked = selectedItems().contains(item.id)
        }
    }
}