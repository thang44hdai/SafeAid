// NewsActivity.kt
package com.example.safeaid.screens.news

import NewsItem
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.ActivityNewsBinding
import com.example.safeaid.core.response.NewsDto
import com.example.safeaid.screens.news.viewmodel.NewsEvent
import com.example.safeaid.screens.news.viewmodel.NewsState
import com.example.safeaid.screens.news.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var adapter: NewsAdapter

    // Lấy ViewModel do Hilt cung cấp
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        observeViewModel()

        // nếu bạn muốn tái tải dữ liệu (khi pull-to-refresh chẳng hạn)
        // viewModel.onTriggerEvent(NewsEvent.LoadNews)
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter { item ->
            startActivity(Intent(this, NewsDetailsActivity::class.java).apply {
                putExtra("news_title", item.title)
                putExtra("news_html",  item.htmlContent)
            })
        }

        binding.rvNewsList.apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter       = this@NewsActivity.adapter
        }

        binding.fabAddNews.setOnClickListener {
            startActivity(Intent(this, AddNewsActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(this) { state ->
            when (state) {
                is NewsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NewsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    // map từ DTO sang UI model rồi đưa vào Adapter
                    val uiList = state.items.map(::toUiModel)
                    adapter.updateList(uiList)
                }
                is NewsState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this,
                        "Lỗi: ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun toUiModel(dto: NewsDto) = NewsItem(
        title        = dto.title,
        timeAgo      = dto.timeAgo,
        thumbnailUrl = dto.thumbnail.orEmpty(),
        htmlContent  = dto.content
    )
}
