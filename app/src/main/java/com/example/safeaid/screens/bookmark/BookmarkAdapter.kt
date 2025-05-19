package com.example.safeaid.screens.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtraining.databinding.ItemBookmarkBinding
import com.example.safeaid.screens.guide.GuideItem

class BookmarkAdapter(
    private val onItemClick: (GuideItem) -> Unit,
    private val onItemLongClick: () -> Unit,
    private val onItemSelected: (String, Boolean) -> Unit
) : ListAdapter<GuideItem, BookmarkAdapter.BookmarkViewHolder>(BookmarkDiffCallback()) {

    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<String>()

    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        selectedItems.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val guide = getItem(position)
        holder.bind(guide)
    }

    inner class BookmarkViewHolder(private val binding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(guide: GuideItem) {
            binding.apply {
                tvTitle.text = guide.title
                tvDescription.text = guide.description
                
                checkbox.isVisible = isSelectionMode
                checkbox.isChecked = selectedItems.contains(guide.id)
                
                btnNext.isVisible = !isSelectionMode

                root.setOnClickListener {
                    if (isSelectionMode) {
                        checkbox.isChecked = !checkbox.isChecked
                        if (checkbox.isChecked) {
                            selectedItems.add(guide.id)
                        } else {
                            selectedItems.remove(guide.id)
                        }
                        onItemSelected(guide.id, checkbox.isChecked)
                    } else {
                        onItemClick(guide)
                    }
                }

                root.setOnLongClickListener {
                    onItemLongClick()
                    true
                }
            }
        }
    }

    class BookmarkDiffCallback : DiffUtil.ItemCallback<GuideItem>() {
        override fun areItemsTheSame(oldItem: GuideItem, newItem: GuideItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GuideItem, newItem: GuideItem): Boolean {
            return oldItem == newItem
        }
    }
}