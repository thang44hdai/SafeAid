// java/com/example/safeaid/screens/community/viewmodel/CommentViewModel.kt
package com.example.safeaid.screens.community.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.CommentDto
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.Prefs
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- State hiển thị lên UI ---
sealed class CommentState {
    object Loading : CommentState()
    data class Success(val items: List<CommentDto>)  : CommentState()
    data class Added  (val comment: CommentDto)      : CommentState()
    data class Error  (val message: String)          : CommentState()
    data class Deleted(val commentId: String) : CommentState()
}

// --- Các event để trigger trong ViewModel ---
sealed class CommentEvent {
    object Load            : CommentEvent()
    data class Post(val content: String) : CommentEvent()
    data class Delete(val commentId: String) : CommentEvent()
}

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val api: ApiService,
    savedState: SavedStateHandle,
    @ApplicationContext private val context: Context
) : BaseViewModel<CommentState, CommentEvent>() {

    private val postId: String = checkNotNull(savedState["post_id"]) {}

    override fun onTriggerEvent(event: CommentEvent) {
        when (event) {
            CommentEvent.Load       -> loadComments()
            is CommentEvent.Post    -> postComment(event.content)
            is CommentEvent.Delete  -> deleteComment(event.commentId)
        }
    }

    // Add new function
    private fun deleteComment(commentId: String) {
        Log.d("CommentViewModel", "deleteComment: $commentId")
        Log.d("CommentViewModel", "POSTID: $postId")
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = {
                    api.deleteComment(
                        bearer = "Bearer ${Prefs.getToken(context)}",
                        postId = postId,
                        commentId = commentId
                    )
                },
                callback = { result ->
                    result.doIfSuccess {
                        updateState(DataResult.Success(CommentState.Deleted(commentId)))
                    }
                    result.doIfFailure { err ->
                        updateState(DataResult.Success(CommentState.Error(err.message ?: "Không thể xóa bình luận")))
                    }
                }
            )
        }
    }

    private fun loadComments() {
        // emit loading state
        updateState(DataResult.Success(CommentState.Loading))
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall  = { api.getComments("Bearer ${Prefs.getToken(context)}", postId) },
                callback = { result ->
                    result.onLoading {
                        updateState(DataResult.Success(CommentState.Loading))
                    }
                    result.doIfSuccess { resp: List<CommentDto> ->
                        // Always treat response as success, even if empty
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
                    bearer = "Bearer ${Prefs.getToken(context)}",
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

}
