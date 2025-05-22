package com.example.safeaid.screens.editprofile.viewmodel

import android.content.Context
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
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

sealed class EditProfileState {
    object Loading : EditProfileState()
    object Success : EditProfileState()
    data class Error(val message: String) : EditProfileState()
    data class UserData(val username: String, val email: String, val phoneNumber: String) : EditProfileState()
}

sealed class EditProfileEvent {
    object LoadUserData : EditProfileEvent()
    data class UpdateProfile(val username: String, val phoneNumber: String) : EditProfileEvent()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userManager: UserManager,
    @ApplicationContext private val context: Context
) : BaseViewModel<EditProfileState, EditProfileEvent>() {

    override fun onTriggerEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.LoadUserData -> loadUserData()
            is EditProfileEvent.UpdateProfile -> updateProfile(event.username, event.phoneNumber)
        }
    }

    private fun loadUserData() {
        val username = userManager.getUsername(context)
        val email = userManager.getEmail(context)
        val phoneNumber = userManager.getPhoneNumber(context)

        updateState(DataResult.Success(EditProfileState.UserData(username, email, phoneNumber)))
    }

    private fun updateProfile(username: String, phoneNumber: String) {
        viewModelScope.launch {
            val token = Prefs.getToken(context) ?: return@launch

            // Create request bodies
            val usernameBody = username.toString()
            val phoneBody = phoneNumber.toString()

            updateState(DataResult.Success(EditProfileState.Loading))

            ApiCaller.safeApiCall(
                apiCall = {
                    apiService.updateProfile(
                        "Bearer $token",
                        usernameBody,
                        phoneBody
                    )
                },
                callback = { result ->
                    result.onLoading {
                        updateState(DataResult.Success(EditProfileState.Loading))
                    }

                    result.doIfSuccess {
                        // Refresh user data after successful update - within a coroutine
                        viewModelScope.launch {
                            userManager.fetchUserInfo(context)
                        }
                        updateState(DataResult.Success(EditProfileState.Success))
                    }

                    result.doIfFailure { error ->
                        updateState(DataResult.Success(EditProfileState.Error(error.message ?: "Unknown error")))
                    }
                }
            )
        }
    }
}