package com.example.safeaid.screens.authenticate.viewmodel

import androidx.lifecycle.*
import com.example.safeaid.core.request.ResetPasswordRequest
import com.example.safeaid.core.response.ErrorResponse
import com.example.safeaid.core.response.ResetPasswordResponse
import com.example.safeaid.core.service.ApiService
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
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
                // Gọi API reset password
                val request = ResetPasswordRequest(email = email)
                val response = apiService.resetPassword(request)
                handleResetPasswordResponse(response, email)
            } catch (e: Exception) {
                _state.value = ForgotPasswordState.Error(
                    e.localizedMessage ?: "Đã xảy ra lỗi khi gửi yêu cầu đặt lại mật khẩu"
                )
            }
        }
    }
    
    private fun handleResetPasswordResponse(response: Response<ResetPasswordResponse>, email: String) {
        if (response.isSuccessful) {
            val body = response.body()
            val message = body?.message ?: "Hướng dẫn đặt lại mật khẩu đã được gửi đến $email"
            _state.value = ForgotPasswordState.Success(message)
        } else {
            // Parse error message từ response body
            val errorBody = response.errorBody()?.string()
            val errorMessage = if (!errorBody.isNullOrEmpty()) {
                try {
                    // Thử parse message lỗi từ JSON
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.error
                } catch (e: Exception) {
                    // Nếu không parse được, hiển thị message mặc định
                    when (response.code()) {
                        400 -> "Email không hợp lệ"
                        404 -> "Không tìm thấy email trong hệ thống"
                        500, 501, 502, 503 -> "Lỗi hệ thống. Vui lòng thử lại sau"
                        else -> "Không thể gửi email đặt lại mật khẩu (${response.code()})"
                    }
                }
            } else {
                // Nếu không có error body, hiển thị message dựa vào HTTP code
                when (response.code()) {
                    400 -> "Email không hợp lệ"
                    404 -> "Không tìm thấy email trong hệ thống"
                    500, 501, 502, 503 -> "Lỗi hệ thống. Vui lòng thử lại sau"
                    else -> "Không thể gửi email đặt lại mật khẩu (${response.code()})"
                }
            }
            _state.value = ForgotPasswordState.Error(errorMessage)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
} 