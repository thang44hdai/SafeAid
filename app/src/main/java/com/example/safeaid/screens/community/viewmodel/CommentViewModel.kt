// java/com/example/safeaid/screens/community/viewmodel/CommentViewModel.kt
package com.example.safeaid.screens.community.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.CommentDto
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- State hiển thị lên UI ---
sealed class CommentState {
    object Loading : CommentState()
    data class Success(val items: List<CommentDto>)  : CommentState()
    data class Added  (val comment: CommentDto)      : CommentState()
    data class Error  (val message: String)          : CommentState()
}

// --- Các event để trigger trong ViewModel ---
sealed class CommentEvent {
    object Load            : CommentEvent()
    data class Post(val content: String) : CommentEvent()
}

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val api: ApiService,
    savedState: SavedStateHandle
) : BaseViewModel<CommentState, CommentEvent>() {

    private val postId: String = checkNotNull(savedState["post_id"]) {
        "post_id must be passed via NavArgs or Intent extras"
    }

    override fun onTriggerEvent(event: CommentEvent) {
        when (event) {
            CommentEvent.Load       -> loadComments()
            is CommentEvent.Post    -> postComment(event.content)
        }
    }

    private fun loadComments() {
        // emit loading state
        updateState(DataResult.Success(CommentState.Loading))
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall  = { api.getComments("Bearer ${getToken()}", postId) },
                callback = { result ->
                    result.onLoading {
                        updateState(DataResult.Success(CommentState.Loading))
                    }
                    result.doIfSuccess { resp: List<CommentDto> ->
                        updateState(DataResult.Success(CommentState.Success(resp)))
                    }
                    result.doIfFailure { err ->
                        updateState(DataResult.Success(CommentState.Error(err.message ?: "Unknown error")))
                    }
                }
            )
        }
    }

    private fun postComment(content: String) {
        // emit loading for posting
        updateState(DataResult.Success(CommentState.Loading))
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall  = { api.createComment(
                    bearer = "Bearer ${getToken()}",
                    postId       = postId,
                    body         = mapOf("content" to content)
                )
                },
                callback = { result ->
                    result.onLoading {
                        updateState(DataResult.Success(CommentState.Loading))
                    }
                    result.doIfSuccess { resp: CommentDto ->
                        updateState(DataResult.Success(CommentState.Added(resp)))
                    }
                    result.doIfFailure { err ->
                        updateState(DataResult.Success(CommentState.Error(err.message ?: "Unknown error")))
                    }
                }
            )
        }
    }

    private fun getToken(): String {
        // TODO: lấy token thật từ SharedPrefs / DataStore
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZGQ5NWE1NDctODAyNC00N2U5LTgzODEtOTFmNjJjOWI4MDM4IiwiZW1haWwiOiJobmFtMTIzQGdtYWlsLmNvbSIsInJvbGUiOiJ1c2VyIiwiaWF0IjoxNzQ2OTQwNDQ0fQ.kuMBBlgYiqhVgvNF1gaM0yCQX61rSbI8vpRem-kEviA"
    }
}
