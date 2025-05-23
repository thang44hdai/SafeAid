package com.example.safeaid.screens.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.ActivitySearchBinding
import com.example.safeaid.screens.home.viewmodel.GuideState
import com.example.safeaid.screens.home.viewmodel.SearchViewModel
import com.example.safeaid.screens.news.NewsActivity
import com.example.safeaid.screens.news.viewmodel.NewsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var guideAdapter: GuideAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Remove underline in SearchView
        val searchPlate = binding.searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchPlate?.setBackgroundColor(Color.TRANSPARENT)

        // Configure News RecyclerView with horizontal layout
        newsAdapter = NewsAdapter(emptyList()) { newsDto ->
            startActivity(
                Intent(this, NewsActivity::class.java)
                    .putExtra("news_id", newsDto.id)
            )
        }

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = newsAdapter
            clipToPadding = false
            setPadding(16, 0, 16, 0)
        }

        // Configure Guide RecyclerView with vertical layout
        guideAdapter = GuideAdapter(emptyList()) { guide ->
//            startActivity(
//                Intent(this, GuideDetailActivity::class.java)
//                    .putExtra("guide_id", guide.guide_id)
//            )
        }

        binding.rvGuideResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = guideAdapter
            clipToPadding = false
            setPadding(16, 0, 16, 0)
        }

        // Setup search view
        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.takeIf { it.isNotBlank() }?.let {
                        performSearch(it)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrBlank()) {
                        binding.tvEmptyState.text = "Nhập từ khóa để tìm kiếm"
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.rvSearchResults.visibility = View.GONE
                        binding.rvGuideResults.visibility = View.GONE
                        binding.tvRelatedNews.visibility = View.GONE
                        binding.tvGuideResults.visibility = View.GONE
                        binding.tvResultCount.visibility = View.GONE
                        viewModel.clearSearch()
                    }
                    return true
                }
            })

            // Focus the search view automatically
            requestFocus()
        }

        // Observe news search results
        viewModel.searchState.observe(this) { state ->
            when (state) {
                is NewsState.Success -> {
                    if (state.items.isEmpty()) {
                        binding.tvRelatedNews.visibility = View.GONE
                        binding.rvSearchResults.visibility = View.GONE
                    } else {
                        binding.tvRelatedNews.visibility = View.VISIBLE
                        binding.rvSearchResults.visibility = View.VISIBLE
                        newsAdapter.updateList(state.items)
                    }
                    updateEmptyState()
                }
                is NewsState.Error -> {
                    binding.tvRelatedNews.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.GONE
                    updateEmptyState()
                }
                NewsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        // Observe guide search results
        viewModel.guideSearchState.observe(this) { state ->
            when (state) {
                is GuideState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (state.guides.isEmpty()) {
                        binding.tvGuideResults.visibility = View.GONE
                        binding.rvGuideResults.visibility = View.GONE
                    } else {
                        binding.tvGuideResults.visibility = View.VISIBLE
                        binding.rvGuideResults.visibility = View.VISIBLE
                        guideAdapter.updateList(state.guides)
                    }
                    updateEmptyState()
                }
                is GuideState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvGuideResults.visibility = View.GONE
                    binding.rvGuideResults.visibility = View.GONE
                    updateEmptyState()
                }
                GuideState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateEmptyState() {
        val hasNewsResults = binding.rvSearchResults.visibility == View.VISIBLE
        val hasGuideResults = binding.rvGuideResults.visibility == View.VISIBLE

        binding.progressBar.visibility = View.GONE

        if (!hasNewsResults && !hasGuideResults) {
            binding.tvResultCount.visibility = View.GONE
            binding.tvEmptyState.text = "Không tìm thấy kết quả"
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            val newsCount = newsAdapter.itemCount
            val guideCount = guideAdapter.itemCount
            val totalCount = newsCount + guideCount

            binding.tvResultCount.text = "Tìm thấy $totalCount kết quả"
            binding.tvResultCount.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }

    private fun performSearch(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmptyState.visibility = View.GONE
        binding.tvResultCount.visibility = View.GONE
        binding.tvRelatedNews.visibility = View.GONE
        binding.tvGuideResults.visibility = View.GONE
        binding.rvSearchResults.visibility = View.GONE
        binding.rvGuideResults.visibility = View.GONE

        viewModel.search(query)
    }
}