// java/com/example/safeaid/screens/community/viewmodel/CommunityViewModel.kt
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

// --- State hiển thị lên UI ---
sealed class CommunityState {
    object Loading : CommunityState()
    data class Success(val posts: List<PostDto>) : CommunityState()
    data class Error(val message: String) : CommunityState()
}

// --- Các event (nếu cần mở rộng sau này) ---
sealed class CommunityEvent {
    object LoadPosts : CommunityEvent()
}

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel<CommunityState, CommunityEvent>() {

    /** Biến track trang hiện tại & tổng trang (nếu API hỗ trợ) */
    var currentPage = 1
    val pageSize = 10
    // giả sử API không trả tổng page thì bạn có thể bỏ

    /** Gọi API lấy danh sách post */
    fun loadPosts(page: Int = currentPage) {
        // emit loading
        updateState(DataResult.Success(CommunityState.Loading))
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getPosts(page, pageSize) },
                callback = { result ->
                    result.onLoading {
                        updateState(DataResult.Success(CommunityState.Loading))
                    }
                    result.doIfSuccess { resp: PostListResponse ->
                        // cập nhật currentPage nếu cần
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

    override fun onTriggerEvent(event: CommunityEvent) {
        when (event) {
            CommunityEvent.LoadPosts -> loadPosts()
        }
    }
}
