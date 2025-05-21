package com.example.safeaid.screens.authenticate.viewmodel

import androidx.lifecycle.*
import com.example.safeaid.core.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    data class Success(val message: String) : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableLiveData<ForgotPasswordState>(ForgotPasswordState.Idle)
    val state: LiveData<ForgotPasswordState> = _state

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _state.value = ForgotPasswordState.Error("Email không được để trống")
            return
        }

        if (!isValidEmail(email)) {
            _state.value = ForgotPasswordState.Error("Email không hợp lệ")
            return
        }

        _state.value = ForgotPasswordState.Loading
        viewModelScope.launch {
            try {
                // Tạm thời giả lập API call thành công
                // TODO: Kết nối với API thực tế khi sẵn sàng
                // val response = apiService.resetPassword(email)
                
                // Đợi 1.5 giây để giả lập thời gian chờ API
                kotlinx.coroutines.delay(1500)
                
                _state.value = ForgotPasswordState.Success(
                    "Hướng dẫn đặt lại mật khẩu đã được gửi đến $email"
                )
            } catch (e: Exception) {
                _state.value = ForgotPasswordState.Error(
                    e.localizedMessage ?: "Đã xảy ra lỗi khi đặt lại mật khẩu"
                )
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
} 