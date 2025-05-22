package com.example.safeaid.screens.changepassword.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.Prefs
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChangePasswordState {
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

sealed class ChangePasswordEvent {
    data class ChangePassword(val oldPassword: String, val newPassword: String) : ChangePasswordEvent()
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : BaseViewModel<ChangePasswordState, ChangePasswordEvent>() {

    override fun onTriggerEvent(event: ChangePasswordEvent) {
        when (event) {
            is ChangePasswordEvent.ChangePassword -> changePassword(event.oldPassword, event.newPassword)
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            val token = Prefs.getToken(context) ?: return@launch

            // Create request map
            val requestBody = mapOf(
                "oldPassword" to oldPassword,
                "newPassword" to newPassword
            )

            updateState(DataResult.Success(ChangePasswordState.Loading))

            ApiCaller.safeApiCall(
                apiCall = {
                    apiService.changePassword(
                        "Bearer $token",
                        requestBody
                    )
                },
                callback = { result ->
                    result.doIfSuccess {
                        updateState(DataResult.Success(ChangePasswordState.Success))
                    }

                    result.doIfFailure { error ->
                        updateState(DataResult.Success(ChangePasswordState.Error(error.message ?: "Đã có lỗi xảy ra")))
                    }
                }
            )
        }
    }
}