package com.example.safeaid.screens.authenticate.viewmodel

import androidx.lifecycle.*
import com.example.safeaid.core.request.RegisterRequest
import com.example.safeaid.core.response.ErrorResponse
import com.example.safeaid.core.response.RegisterResponse
import com.example.safeaid.core.service.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

sealed class RegisterState {
    object Idle    : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val error: String)   : RegisterState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _state = MutableLiveData<RegisterState>(RegisterState.Idle)
    val state: LiveData<RegisterState> = _state

    fun register(username: String, email: String, phone: String, password: String) {
        // Validate username
        if (username.isEmpty()) {
            _state.value = RegisterState.Error("Tên đăng nhập không được để trống")
            return
        }
        
        // Validate email
        if (email.isEmpty()) {
            _state.value = RegisterState.Error("Email không được để trống")
            return
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            _state.value = RegisterState.Error("Email không hợp lệ")
            return
        }
        
        // Validate phone
        if (phone.isEmpty()) {
            _state.value = RegisterState.Error("Số điện thoại không được để trống")
            return
        }
        
        // Validate phone format
        if (!isValidPhone(phone)) {
            _state.value = RegisterState.Error("Số điện thoại không hợp lệ")
            return
        }
        
        // Validate password
        if (password.isEmpty()) {
            _state.value = RegisterState.Error("Mật khẩu không được để trống")
            return
        }
        
        // Validate password length
        if (password.length < 6) {
            _state.value = RegisterState.Error("Mật khẩu phải có ít nhất 6 ký tự")
            return
        }
                
        _state.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val req = RegisterRequest(
                    username    = username,
                    email       = email,
                    password    = password,
                    phoneNumber = phone
                )
                val resp: Response<RegisterResponse> = api.register(req)
                handleRegisterResponse(resp)
            } catch (e: Exception) {
                _state.value = RegisterState.Error(e.localizedMessage ?: "Lỗi kết nối mạng. Vui lòng thử lại sau.")
            }
        }
    }
    
    private fun handleRegisterResponse(response: Response<RegisterResponse>) {
        if (response.isSuccessful) {
            val body = response.body()!!
            _state.value = RegisterState.Success(body.message)
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
                        400 -> "Thông tin đăng ký không hợp lệ"
                        409 -> "Email hoặc số điện thoại đã được sử dụng"
                        500, 501, 502, 503 -> "Lỗi hệ thống. Vui lòng thử lại sau"
                        else -> "Đăng ký thất bại (${response.code()})"
                    }
                }
            } else {
                // Nếu không có error body, hiển thị message dựa vào HTTP code
                when (response.code()) {
                    400 -> "Thông tin đăng ký không hợp lệ"
                    409 -> "Email hoặc số điện thoại đã được sử dụng"
                    500, 501, 502, 503 -> "Lỗi hệ thống. Vui lòng thử lại sau"
                    else -> "Đăng ký thất bại (${response.code()})"
                }
            }
            _state.value = RegisterState.Error(errorMessage)
        }
    }
    
    // Check if email has valid format
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    // Check if phone number has valid format
    private fun isValidPhone(phone: String): Boolean {
        return phone.length >= 9 && phone.length <= 11 && phone.all { it.isDigit() }
    }
}