package com.example.safeaid.screens.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtraining.databinding.ItemNewsBinding

class NewsAdapter(
    private val newsList: List<NewsItem>,
    private val onItemClick: (NewsItem) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(private val binding: ItemNewsBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsItem) {
            binding.tvNewsTitle.text = item.title
            binding.tvNewsTime.text = item.timeAgo
            // nếu bạn muốn load ảnh từ URL, bật Glide/Picasso ở đây:
            // Glide.with(binding.ivNewsThumbnail).load(item.thumbnailUrl).into(binding.ivNewsThumbnail)

            // khi click bất kỳ đâu hoặc “Xem chi tiết” thì gọi onItemClick
            binding.root.setOnClickListener { onItemClick(item) }
            binding.tvNewsReadMore.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size
}
