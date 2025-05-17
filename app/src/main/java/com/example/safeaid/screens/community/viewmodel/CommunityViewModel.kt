package com.example.safeaid.screens.community.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.PostListResponse
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import com.example.safeaid.screens.community.data.PostDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class CommunityState {
    object Loading : CommunityState()
    data class Success(val posts: List<PostDto>) : CommunityState()
    data class Error(val message: String) : CommunityState()
}


sealed class CommunityEvent {
    object LoadPosts : CommunityEvent()
    data class LikePost(val postId: String) : CommunityEvent()
    data class UnLikePost(val postId: String) : CommunityEvent()
}

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel<CommunityState, CommunityEvent>() {

    var currentPage = 1
    val pageSize = 10


    fun loadPosts(page: Int = currentPage) {
        // emit loading
        updateState(DataResult.Success(CommunityState.Loading))
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getPosts("Bearer ${getToken()}", page, pageSize) },
                callback = { result ->
                    result.onLoading {
                        updateState(DataResult.Success(CommunityState.Loading))
                    }
                    result.doIfSuccess { resp: PostListResponse ->
                        currentPage = resp.page
                        updateState(DataResult.Success(CommunityState.Success(resp.posts)))
                    }
                    result.doIfFailure { error ->
                        updateState(DataResult.Success(CommunityState.Error(error.message ?: "Unknown error")))
                    }
                }
            )
        }
    }

    private fun likePost(postId: String) {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.likePost("Bearer ${getToken()}", postId) },
                callback = { result ->
                    result.doIfSuccess {
                        updateLocalLike(postId, liked = true)
                    }
                }
            )
        }
    }

    private fun unLikePost(postId: String) {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.unLikePost("Bearer ${getToken()}", postId) },
                callback = { result ->
                    result.doIfSuccess {
                        updateLocalLike(postId, liked = false)
                    }
                }
            )
        }
    }

    private fun updateLocalLike(postId: String, liked: Boolean) {
        val dr = viewState.value
        if (dr is DataResult.Success<*>) {
            val state = dr.data
            if (state is CommunityState.Success) {
                val updated = state.posts.toMutableList()
                val idx = updated.indexOfFirst { it.post_id == postId }
                if (idx != -1) {
                    val p = updated[idx]
                    updated[idx] = p
                    updateState(DataResult.Success(CommunityState.Success(updated)))
                }
            }
        }
    }

    override fun onTriggerEvent(event: CommunityEvent) {
        when (event) {
            CommunityEvent.LoadPosts        -> loadPosts()
            is CommunityEvent.LikePost      -> likePost(event.postId)
            is CommunityEvent.UnLikePost    -> unLikePost(event.postId)
        }
    }

    private fun getToken(): String {
        // TODO: lấy token thật từ SharedPrefs / DataStore
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZGQ5NWE1NDctODAyNC00N2U5LTgzODEtOTFmNjJjOWI4MDM4IiwiZW1haWwiOiJobmFtMTIzQGdtYWlsLmNvbSIsInJvbGUiOiJ1c2VyIiwiaWF0IjoxNzQ2OTQwNDQ0fQ.kuMBBlgYiqhVgvNF1gaM0yCQX61rSbI8vpRem-kEviA"
    }
}
