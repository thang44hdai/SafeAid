package com.example.safeaid.screens.news

import NewsItem
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.ActivityNewsBinding
import com.example.androidtraining.R

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Data mẫu
        val sampleList = listOf(
            NewsItem(
                title = "VinMec: Nguyên tắc so cấp cứu và kế hoạch thực hiện",
                timeAgo = "10 phút trước",
                thumbnailUrl = "" // Hoặc link URL, ví dụ "https://example.com/news_1.png"
            ),
            NewsItem(
                title = "Bộ giáo dục và đào tạo - Tài liệu hướng dẫn sơ cứu cơ bản",
                timeAgo = "20 phút trước",
                thumbnailUrl = ""
            ),
            NewsItem(
                title = "VinMec: Nguyên tắc so cấp cứu và kế hoạch thực hiện",
                timeAgo = "30 phút trước",
                thumbnailUrl = ""
            ),
            NewsItem(
                title = "Bộ giáo dục và đào tạo - Tài liệu hướng dẫn sơ cứu cơ bản",
                timeAgo = "40 phút trước",
                thumbnailUrl = ""
            )
        )

        val newsAdapter = NewsAdapter(sampleList) { item ->
            // Xử lý khi click item, ví dụ: mở chi tiết tin tức
            // startActivity(Intent(this, NewsDetailActivity::class.java))
        }

        binding.rvNewsList.layoutManager = LinearLayoutManager(this)
        binding.rvNewsList.adapter = newsAdapter
    }
}
