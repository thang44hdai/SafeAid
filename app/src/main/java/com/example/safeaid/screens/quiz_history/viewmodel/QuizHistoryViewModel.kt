package com.example.safeaid.screens.quiz_history.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.Category
import com.example.safeaid.core.response.Question
import com.example.safeaid.core.response.QuizHistoryDetailResponse
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.Prefs.getToken
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import com.example.safeaid.screens.quiz.data.FilterCriteria
import com.example.safeaid.screens.quiz_history.data.QuizHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class QuizHistoryViewModel @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : BaseViewModel<QuizHistoryState, QuizHistoryEvent>() {
    var listCategory: MutableList<Category> = mutableListOf()
    var currentFilter = FilterCriteria()

    fun getHistoryQuiz() {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val categoryDeferred = async { apiService.getCategoryQuiz() }
                    val historyDeferred =
                        async { apiService.getResultQuiz("Bearer ${getToken(context)}") }

                    val categoryResponse = categoryDeferred.await()
                    val historyResponse = historyDeferred.await()

                    if (categoryResponse.isSuccessful && historyResponse.isSuccessful) {
                        val categoryData = categoryResponse.body()!!
                        val historyData = historyResponse.body()!!
                        listCategory = categoryData.categories.toMutableList()

                        historyData.sortQuizAttemptsByCompletedAt()
                        val quizFilterItems: List<QuizHistory> = historyData.quizAttempts?.map {
                            val cate =
                                categoryData.categories.firstOrNull { it1 -> it1.quizzes.any { it2 -> it2.quizId == it.quizId } }

                            QuizHistory(quizAttempt = it, category = cate)
                        } ?: listOf()
                        updateState(
                            DataResult.Success(
                                QuizHistoryState.HistoryQuestions(quizFilterItems)
                            )
                        )
                    } else {

                    }
                }
            } catch (e: SocketTimeoutException) {
            } catch (e: Exception) {
            }
        }
    }

    fun getHistoryDetail(quizAttemptId: String, quizId: String) {
        viewModelScope.launch {
            ApiCaller.safeApiCall<QuizHistoryDetailResponse>(
                apiCall = {
                    apiService.getHistoryDetail(quizAttemptId, quizId)
                },
                callback = { result ->
                    result.doIfSuccess {
                        val listUserAnswer = it.userAnswers
                        val data = it.questions?.map { it1 ->
                            val ques =
                                listUserAnswer?.firstOrNull { item -> item.questionId == it1.questionId }
                            if (ques != null) {
                                it1.copy(selectedAnswerId = ques.selectedAnswerId)
                            } else {
                                it1.copy()
                            }
                        }
                        Log.i("hihihi", "${data}")
                        updateState(
                            DataResult.Success(
                                QuizHistoryState.HistoryDetail(
                                    data ?: listOf()
                                )
                            )
                        )
                    }
                    result.doIfFailure {
                    }
                    result.onLoading { }
                }
            )
        }
    }

    override fun onTriggerEvent(event: QuizHistoryEvent) {
    }
}

sealed class QuizHistoryState {
    class HistoryQuestions(val quizzes: List<QuizHistory>) : QuizHistoryState()
    class HistoryDetail(val data: List<Question>) : QuizHistoryState()
}

sealed class QuizHistoryEvent {}