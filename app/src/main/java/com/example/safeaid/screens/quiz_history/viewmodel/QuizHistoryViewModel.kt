package com.example.safeaid.screens.quiz_history.viewmodel

import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuizHistoryViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel<QuizHistoryState, QuizHistoryEvent>() {
    fun getHistoryOfQuiz() {

    }

    override fun onTriggerEvent(event: QuizHistoryEvent) {
    }
}

sealed class QuizHistoryState {}
sealed class QuizHistoryEvent {}