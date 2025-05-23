package com.example.safeaid.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.safeaid.core.response.Guide

class GuideAdapter(
    private var guides: List<Guide>,
    private val onItemClick: (Guide) -> Unit
) : RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guides, parent, false)
        return GuideViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(guides[position])
    }

    override fun getItemCount(): Int = guides.size

    fun updateList(newGuides: List<Guide>) {
        guides = newGuides
        notifyDataSetChanged()
    }

    inner class GuideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvGuideTitle)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvGuideCategory)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvGuideDescription)
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.ivGuideThumbnail)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(guides[position])
                }
            }
        }

        fun bind(guide: Guide) {
            tvTitle.text = guide.title
            tvCategory.text = guide.category?.name ?: "Chưa phân loại"
            tvDescription.text = guide.description

            // Load thumbnail if available
            if (guide.thumbnailPath != null && guide.thumbnailPath.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(guide.thumbnailPath)
                    .placeholder(R.drawable.ic_guide) // Use an existing placeholder image
                    .error(R.drawable.ic_guide) // Use an existing error image
                    .into(ivThumbnail)
            } else {
                ivThumbnail.setImageResource(R.drawable.ic_guide) // Use an existing default image
            }
        }
    }
}