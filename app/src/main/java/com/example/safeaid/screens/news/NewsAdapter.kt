package com.example.safeaid.screens.news

import NewsItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtraining.R
import com.bumptech.glide.Glide
import com.example.androidtraining.databinding.ItemNewsBinding
import kotlin.random.Random

// import đúng NewsItem
// (bỏ dòng `import NewsItem`)
class NewsAdapter(
    private val onItemClick: (NewsItem) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val newsList = mutableListOf<NewsItem>()

    fun updateList(newList: List<NewsItem>) {
        newsList.clear()
        newsList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsItem) {
            //Gùng Glide để tải ảnh từ URL
            Glide.with(binding.ivNewsThumbnail.context)
                .load(item.thumbnailUrl)
                .into(binding.ivNewsThumbnail)

            binding.tvNewsTitle.text = item.title
            binding.tvNewsTime.text  = item.timeAgo
            binding.root.setOnClickListener       { onItemClick(item) }
            binding.tvNewsReadMore.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NewsViewHolder(
            ItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size
}
