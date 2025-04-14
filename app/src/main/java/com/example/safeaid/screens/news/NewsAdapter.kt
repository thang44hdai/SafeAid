package com.example.safeaid.screens.news

import NewsItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtraining.databinding.ItemNewsBinding

class NewsAdapter(
    private val newsList: List<NewsItem>,
    private val onItemClick: (NewsItem) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsItem) {
            binding.tvNewsTitle.text = item.title
            binding.tvNewsTime.text = item.timeAgo
            // Dùng Glide hoặc Picasso nếu load ảnh từ URL
            // Glide.with(binding.ivNewsThumbnail.context).load(item.thumbnailUrl).into(binding.ivNewsThumbnail)

            // Hoặc nếu bạn có resource cục bộ, set qua android:src
            // binding.ivNewsThumbnail.setImageResource(R.drawable.news_1)

            binding.tvNewsReadMore.setOnClickListener {
                // Xử lý sự kiện bấm "Xem chi tiết" nếu muốn
                onItemClick(item)
            }

            binding.root.setOnClickListener {
                // Xử lý sự kiện click cả item
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNewsBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size
}
