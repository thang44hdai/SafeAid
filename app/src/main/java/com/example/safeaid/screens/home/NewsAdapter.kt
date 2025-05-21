package com.example.safeaid.screens.home

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtraining.databinding.ItemNewsBinding
import com.example.androidtraining.databinding.ItemNewsHomeBinding
import com.example.safeaid.core.response.NewsDto
import com.example.safeaid.core.utils.convertTime

class NewsAdapter(
    private var items: List<NewsDto>,
    private val onClick: (NewsDto) -> Unit
) : RecyclerView.Adapter<NewsAdapter.VH>() {

    fun updateList(new: List<NewsDto>) {
        items = new
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemNewsHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemNewsHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dto: NewsDto) {
            // load thumbnail
            Glide.with(binding.ivThumb)
                .load(dto.thumbnail)
                .into(binding.ivThumb)

            binding.tvTitle.text = dto.title

            // relative time (nếu dto.createdAt là ISO string)
            dto.createdAt?.let { iso ->
                val then = convertTime(iso)
                binding.tvTime.text = then
            }

            binding.root.setOnClickListener { onClick(dto) }
        }
    }
}