package com.example.safeaid.screens.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtraining.databinding.ItemGuideCategoryBinding
import com.example.safeaid.core.response.GuideCategoryResponse

class GuideCategoryAdapter(
    private val onCategoryClick: (GuideCategoryResponse) -> Unit
) : ListAdapter<GuideCategoryResponse, GuideCategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemGuideCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemGuideCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: GuideCategoryResponse) {
            binding.apply {
                tvCategoryName.text = category.name
                root.setOnClickListener { onCategoryClick(category) }
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<GuideCategoryResponse>() {
        override fun areItemsTheSame(oldItem: GuideCategoryResponse, newItem: GuideCategoryResponse): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: GuideCategoryResponse, newItem: GuideCategoryResponse): Boolean {
            return oldItem == newItem
        }
    }
}