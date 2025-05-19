package com.example.safeaid.screens.authenticate.viewmodel

import androidx.lifecycle.*
import com.example.safeaid.core.request.RegisterRequest
import com.example.safeaid.core.response.RegisterResponse
import com.example.safeaid.core.service.ApiService
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
                if (resp.isSuccessful) {
                    val body = resp.body()!!
                    _state.value = RegisterState.Success(body.message)
                } else {
                    _state.value = RegisterState.Error("Lỗi server: ${resp.code()}")
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error(e.localizedMessage ?: "Lỗi không xác định")
            }
        }
    }
}