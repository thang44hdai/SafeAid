package com.example.safeaid.screens.quiz.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.request.QuizAttemptRequest
import com.example.safeaid.core.response.Category
import com.example.safeaid.core.response.QuizAttemptResponse
import com.example.safeaid.core.response.QuizResponse
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizCategoryViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel<QuizCategoryState, QuizCategoryEvent>() {
    var selectedQuizCategoryPostition: Int = 0
    var currentPage = 1
    var maxPage = 1
    val pageSize = 2

    fun getQuizCategory() {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getCategoryQuiz() },
                callback = { result ->
                    result.doIfSuccess {
                        updateState(DataResult.Success(QuizCategoryState.ListCategory(it.categories)))
                    }
                    result.doIfFailure {
                    }
                    result.onLoading { }
                }
            )
        }
    }

    fun getQuestionByQuiz(quizId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getQuestionByQuizId(quizId) },
                callback = { result ->
                    result.doIfSuccess {
                        updateState(DataResult.Success(QuizCategoryState.QuestionsByQuizId(it)))
                    }
                    result.doIfFailure {
                    }
                    result.onLoading { }
                }
            )
        }
    }

    fun saveQuizResult(request: QuizAttemptRequest, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            ApiCaller.safeApiCall(
                apiCall = { apiService.saveResultQuiz(request) },
                callback = { result ->
                    result.doIfSuccess {
                        callback.invoke()
                    }
                    result.doIfFailure {
                    }
                    result.onLoading { }
                }
            )
        }
    }

    fun getHistoryQuiz() {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getResultQuiz("5e7e033d-c4e7-42bc-88e5-dbc2555e38a3") },
                callback = { result ->
                    result.doIfSuccess {
                        it.sortQuizAttemptsByCompletedAt()
                        updateState(DataResult.Success(QuizCategoryState.HistoryQuestions(it)))
                    }
                    result.doIfFailure {
                    }
                    result.onLoading { }
                }
            )
        }
    }

    override fun onTriggerEvent(event: QuizCategoryEvent) {
    }
}

sealed class QuizCategoryState {
    class ListCategory(val categories: List<Category>) : QuizCategoryState()
    class QuestionsByQuizId(val questions: QuizResponse) : QuizCategoryState()
    class HistoryQuestions(val quizzes: QuizAttemptResponse) : QuizCategoryState()
}

sealed class QuizCategoryEvent {}