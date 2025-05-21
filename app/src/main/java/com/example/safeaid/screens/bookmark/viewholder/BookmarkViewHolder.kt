package com.example.safeaid.screens.bookmark.viewholder

import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import com.example.safeaid.screens.guide.GuideItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel<BookmarkState, BookmarkEvent>() {

    private val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZTJhNDAzNjQtZTBmNS00YjBmLWIyNDAtZWY0ODM0NTBlMmFkIiwiZW1haWwiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZSI6InVzZXIiLCJpYXQiOjE3NDYwMTgxMTR9.1R9cmZKSC6hED9SWaNdJR0_TC82nk5_QYopGeleyFMI"
    
    private val _bookmarks = MutableStateFlow<List<GuideItem>>(emptyList())
    val bookmarks: StateFlow<List<GuideItem>> = _bookmarks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadBookmarks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            ApiCaller.safeApiCall(
                apiCall = { apiService.getFavouriteList(token) },
                callback = { result ->
                    result.doIfSuccess { response ->
                        val guideItems = response.items.map { item ->
                            GuideItem(
                                id = item.guide.guideId,
                                title = item.guide.title,
                                description = item.guide.description,
                                thumbnailPath = item.guide.thumbnailPath
                            )
                        }
                        _bookmarks.value = guideItems
                        updateState(DataResult.Success(BookmarkState.ListBookmarks(guideItems)))
                    }
                    result.doIfFailure {}
                    result.onLoading {}
                }
            )
        }
    }

    fun deleteBookmark(guideId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            ApiCaller.safeApiCall(
                apiCall = { apiService.deleteFavouriteGuide(guideId, token) },
                callback = { result ->
                    result.doIfSuccess { 
                        // Reload danh sách sau khi xóa thành công
                        loadBookmarks()
                    }
                    result.doIfFailure { error ->
                        _error.value = error.message
                        updateState(DataResult.Error(error))
                    }
                }
            )
        }
    }
    override fun onTriggerEvent(event: BookmarkEvent) {
        when (event) {
            is BookmarkEvent.LoadBookmarks -> loadBookmarks()
        }
    }
}

sealed class BookmarkState {
    data class ListBookmarks(val bookmarks: List<GuideItem>) : BookmarkState()
    data class Error(val message: String) : BookmarkState()
    object Loading : BookmarkState()
}

sealed class BookmarkEvent {
    object LoadBookmarks : BookmarkEvent()
}