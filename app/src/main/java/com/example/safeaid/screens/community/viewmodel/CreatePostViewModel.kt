// java/com/example/safeaid/screens/community/viewmodel/CreatePostViewModel.kt
package com.example.safeaid.screens.community.viewmodel

import androidx.lifecycle.*
import com.example.safeaid.core.response.CreatePostResponse
import com.example.safeaid.core.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

sealed class CreatePostState {
    object Idle    : CreatePostState()
    object Loading : CreatePostState()
    data class Success(val response: CreatePostResponse) : CreatePostState()
    data class Error(val message: String)              : CreatePostState()
}

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableLiveData<CreatePostState>(CreatePostState.Idle)
    val state: LiveData<CreatePostState> = _state

    /**
     * @param token   your "Bearer ..." JWT
     * @param content non-empty post body
     * @param title   optional short title
     * @param images  zero or more `MultipartBody.Part` created in the Activity
     */
    fun createPost(
        token: String,
        content: RequestBody,
        title: RequestBody?,
        images: List<MultipartBody.Part>
    ) {
        _state.value = CreatePostState.Loading

        viewModelScope.launch {
            try {
                val response = apiService.createPost(
                    bearerToken = token,
                    content     = content,
                    title       = title ?: "".toRequestBody("text/plain".toMediaType()),
                    images      = images
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        _state.value = CreatePostState.Success(it)
                    } ?: run {
                        _state.value = CreatePostState.Error("Empty body")
                    }
                } else {
                    _state.value = CreatePostState.Error("Error ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = CreatePostState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
