package com.example.safeaid.screens.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.androidtraining.databinding.ItemBookmarkBinding
import com.example.safeaid.screens.bookmark.viewholder.BookmarkViewHolder
import com.example.safeaid.screens.guide.GuideItem

class BookmarkAdapter(
    private val onItemClick: (GuideItem) -> Unit,
    private val onItemLongClick: () -> Unit,
    private val onItemSelected: (String, Boolean) -> Unit
) : ListAdapter<GuideItem, BookmarkViewHolder>(BookmarkDiffCallback()) {

    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<String>()

    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        if (!enabled) {
            selectedItems.clear()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookmarkViewHolder(
            binding = binding,
            onItemClick = onItemClick,
            onItemLongClick = onItemLongClick,
            onItemSelected = onItemSelected,
            isSelectionMode = { isSelectionMode },
            selectedItems = { selectedItems },
            getItem = { position -> getItem(position) }
        )
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(getItem(position))
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