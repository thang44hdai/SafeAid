package com.example.safeaid.screens.person.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.Prefs
import com.example.safeaid.core.utils.UserManager
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

sealed class AvatarUploadState {
    object Loading : AvatarUploadState()
    data class Success(val uri: Uri) : AvatarUploadState()
    data class Error(val message: String) : AvatarUploadState()
}

sealed class AvatarUploadEvent {
    data class UploadAvatar(val uri: Uri) : AvatarUploadEvent()
}

@HiltViewModel
class UpdateAvatarViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userManager: UserManager,
    @ApplicationContext private val context: Context
): BaseViewModel<AvatarUploadState, AvatarUploadEvent>() {

    private val _uploadState = MutableLiveData<AvatarUploadState>()
    val uploadState: LiveData<AvatarUploadState> = _uploadState

    fun uploadAvatar(context: Context, imageUri: Uri) {
        if (!isValidImageType(context, imageUri)) {
            _uploadState.value = AvatarUploadState.Error("Chỉ chấp nhận file hình ảnh! Các định dạng cho phép: JPG, PNG, GIF, WEBP")
            return
        }

        _uploadState.value = AvatarUploadState.Loading
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = {
                    val file = uriToFile(context, imageUri)
                    val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
                    val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData("avatar", file.name, requestBody)
                    apiService.updateAvatar("Bearer ${Prefs.getToken(context)}", filePart)
                },
                callback = { result ->
                    result.onLoading {
                        _uploadState.value = AvatarUploadState.Loading
                    }
                    result.doIfSuccess { _ ->
                        // After successful upload, refresh user info to get the new avatar URL
                        viewModelScope.launch {
                            val success = userManager.fetchUserInfo(context)
                            _uploadState.value = AvatarUploadState.Success(imageUri)
                        }
                    }
                    result.doIfFailure { error ->
                        _uploadState.value = AvatarUploadState.Error(error.message ?: "Unknown error")
                    }
                }
            )
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("avatar", ".jpg", context.cacheDir)

        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }

    private fun isValidImageType(context: Context, uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri)
        return mimeType != null && arrayOf(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
        ).contains(mimeType.lowercase())
    }

    override fun onTriggerEvent(event: AvatarUploadEvent) {
        when (event) {
            is AvatarUploadEvent.UploadAvatar -> uploadAvatar(context, event.uri)
        }
    }
}