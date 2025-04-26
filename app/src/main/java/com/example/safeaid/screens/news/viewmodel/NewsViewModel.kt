package com.example.safeaid.screens.news.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.network.NewsRepository
import com.example.safeaid.core.response.NewsDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// State hiển thị lên UI
sealed class NewsState {
    object Loading : NewsState()
    data class Success(val items: List<NewsDto>) : NewsState()
    data class Error(val message: String) : NewsState()
}

// Các event để trigger trong ViewModel (nếu cần sau này)
sealed class NewsEvent {
    object LoadNews : NewsEvent()
}

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repo: NewsRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<NewsState>(NewsState.Loading)
    val viewState: LiveData<NewsState> = _viewState

    init {
        loadNews()
    }

    fun loadNews() {
        _viewState.value = NewsState.Loading
        viewModelScope.launch {
            try {
                val resp = repo.fetchAllNews()
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null) {
                        _viewState.value = NewsState.Success(body.data)
                    } else {
                        _viewState.value = NewsState.Error("Empty response")
                    }
                } else {
                    _viewState.value = NewsState.Error("Error ${resp.code()}")
                }
            } catch (e: Exception) {
                _viewState.value = NewsState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
