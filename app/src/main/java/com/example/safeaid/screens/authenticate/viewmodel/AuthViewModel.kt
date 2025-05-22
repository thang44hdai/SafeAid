package com.example.safeaid.screens.authenticate.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.safeaid.core.request.LoginRequest
import com.example.safeaid.core.response.ErrorResponse
import com.example.safeaid.core.response.LoginResponse
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.Prefs
import com.google.gson.Gson
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val token: String): AuthState()
    data class Error(val message: String): AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _state = MutableLiveData<AuthState>(AuthState.Idle)
    val state: LiveData<AuthState> = _state

    fun login(email: String, password: String) {
        // Validate email
        if (email.isEmpty()) {
            _state.value = AuthState.Error("Email không được để trống")
            return
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            _state.value = AuthState.Error("Email không hợp lệ")
            return
        }
        
        // Validate password
        if (password.isEmpty()) {
            _state.value = AuthState.Error("Mật khẩu không được để trống")
            return
        }
        
        _state.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val resp = api.login(LoginRequest(email, password))
                handleLoginResponse(resp)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.localizedMessage ?: "Lỗi kết nối mạng. Vui lòng thử lại sau.")
            }
        }
    }
    
    private fun handleLoginResponse(response: Response<LoginResponse>) {
        if (response.isSuccessful) {
            val body = response.body()!!
            // Lưu token
            Prefs.saveToken(appContext, body.token)
            _state.value = AuthState.Success(body.token)
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
                        401 -> "Thông tin đăng nhập không hợp lệ"
                        403 -> "Bạn không có quyền truy cập"
                        404 -> "Tài khoản không tồn tại"
                        500, 501, 502, 503 -> "Lỗi hệ thống. Vui lòng thử lại sau"
                        else -> "Đăng nhập thất bại (${response.code()})"
                    }
                }
            } else {
                // Nếu không có error body, hiển thị message dựa vào HTTP code
                when (response.code()) {
                    401 -> "Thông tin đăng nhập không hợp lệ"
                    403 -> "Bạn không có quyền truy cập"
                    404 -> "Tài khoản không tồn tại"
                    500, 501, 502, 503 -> "Lỗi hệ thống. Vui lòng thử lại sau"
                    else -> "Đăng nhập thất bại (${response.code()})"
                }
            }
            _state.value = AuthState.Error(errorMessage)
        }
    }
    
    // Check if email has valid format
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}