package com.example.safeaid.screens.authenticate.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.safeaid.core.request.LoginRequest
import com.example.safeaid.core.response.LoginResponse
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.Prefs
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
        _state.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val resp = api.login(LoginRequest(email, password))
                if (resp.isSuccessful) {
                    val body = resp.body()!!
                    _state.value = AuthState.Success(body.token)
                    // TODO: lưu token vào SharedPreferences / DataStore
                    val jwt = resp.body()!!.token
                    // lưu token
                    Prefs.saveToken(appContext, jwt)
                    _state.value = AuthState.Success(jwt)
                } else {
                    _state.value = AuthState.Error("Lỗi ${resp.code()}")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.localizedMessage ?: "Lỗi mạng")
            }
        }
    }
}