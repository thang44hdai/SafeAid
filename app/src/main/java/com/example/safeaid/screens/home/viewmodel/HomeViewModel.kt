package com.example.safeaid.screens.home.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import com.example.safeaid.screens.quiz.viewmodel.SearchQuizState
import com.example.safeaid.screens.quiz_history.data.QuizHistory
import com.example.safeaid.screens.quiz_history.viewmodel.QuizHistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel<HomeState, HomeEvent>() {

    fun getNotification() {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getNotification() },
                callback = { result ->
                    result.doIfSuccess {
                        Log.i("hihihi", "success")
                    }
                    result.doIfFailure {
                    }
                    result.onLoading { }
                }
            )
        }
    }

    override fun onTriggerEvent(event: HomeEvent) {

    }
}

sealed class HomeState {}
sealed class HomeEvent {}