package com.example.safeaid.screens.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.response.Guide
import com.example.safeaid.core.response.NewsDto
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.screens.news.viewmodel.NewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _searchState = MutableLiveData<NewsState>()
    val searchState: LiveData<NewsState> = _searchState

    private val _guideSearchState = MutableLiveData<GuideState>()
    val guideSearchState: LiveData<GuideState> = _guideSearchState

    fun searchNews(query: String, page: Int = 1, limit: Int = 10) {
        _searchState.value = NewsState.Loading

        viewModelScope.launch {
            try {
                val response = apiService.searchNews(
                    keyword = query,
                    page = page,
                    limit = limit
                )

                if (response.isSuccessful) {
                    response.body()?.let { newsResponse ->
                        _searchState.value = NewsState.Success(newsResponse.data)
                    } ?: run {
                        _searchState.value = NewsState.Error("Không có dữ liệu")
                    }
                } else {
                    _searchState.value = NewsState.Error("Lỗi: ${response.code()}")
                }
            } catch (e: Exception) {
                _searchState.value = NewsState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun searchGuides(query: String, page: Int = 1, limit: Int = 10) {
        _guideSearchState.value = GuideState.Loading

        viewModelScope.launch {
            try {
                val response = apiService.searchGuides(
                    keyword = query,
                    page = page,
                    limit = limit
                )

                if (response.isSuccessful) {
                    response.body()?.let { guideResponse ->
                        _guideSearchState.value = GuideState.Success(guideResponse.guides)
                    } ?: run {
                        _guideSearchState.value = GuideState.Error("Không có dữ liệu")
                    }
                } else {
                    _guideSearchState.value = GuideState.Error("Lỗi: ${response.code()}")
                }
            } catch (e: Exception) {
                _guideSearchState.value = GuideState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun search(query: String) {
        searchNews(query)
        searchGuides(query)
    }

    fun clearSearch() {
        _searchState.value = NewsState.Success(emptyList())
        _guideSearchState.value = GuideState.Success(emptyList())
    }
}

sealed class GuideState {
    object Loading : GuideState()
    data class Success(val guides: List<Guide>) : GuideState()
    data class Error(val message: String) : GuideState()
}