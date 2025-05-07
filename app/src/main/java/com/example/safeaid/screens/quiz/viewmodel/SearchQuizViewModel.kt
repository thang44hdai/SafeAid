package com.example.safeaid.screens.quiz.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.Category
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import com.example.safeaid.screens.quiz.data.QuizFilterItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class SearchQuizViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel<SearchQuizState, SearchQuizEvent>() {

    fun getQuizCategory() {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getCategoryQuiz() },
                callback = { result ->
                    result.doIfSuccess {
                        updateState(DataResult.Success(SearchQuizState.ListCategory(it.categories)))
                    }
                    result.doIfFailure {
                    }
                    result.onLoading { }
                }
            )
        }
    }

    fun getData() {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val categoryDeferred = async { apiService.getCategoryQuiz() }
                    val resultDeferred =
                        async { apiService.getResultQuiz("5e7e033d-c4e7-42bc-88e5-dbc2555e38a3") }

                    val categoryResponse = categoryDeferred.await()
                    val resultResponse = resultDeferred.await()

                    if (categoryResponse.isSuccessful && resultResponse.isSuccessful) {
                        val categoryData = categoryResponse.body()!!
                        val resultData = resultResponse.body()!!

                        val quizFilterItems: List<QuizFilterItem> =
                            categoryData.categories.flatMap { category ->
                                category.quizzes.map { quiz ->
                                    QuizFilterItem(
                                        quiz = quiz,
                                        category = category,
                                        isDone = resultData.quizAttempts?.any { it.quizId == quiz.quizId }
                                            ?: false,
                                        quizAttempt = resultData.quizAttempts?.firstOrNull { it.quizId == quiz.quizId }
                                    )
                                }
                            }
                        updateState(DataResult.Success(SearchQuizState.ListFilerItem(quizFilterItems)))
                    } else {
                        val errorMessage =
                            categoryResponse.errorBody()?.string() ?: resultResponse.errorBody()
                                ?.string()
                    }
                }
            } catch (e: SocketTimeoutException) {
            } catch (e: Exception) {
            }
        }

    }

    override fun onTriggerEvent(event: SearchQuizEvent) {
    }
}

sealed class SearchQuizState {
    class ListFilerItem(val data: List<QuizFilterItem>) : SearchQuizState()
    class ListCategory(val data: List<Category>) : SearchQuizState()

}

sealed class SearchQuizEvent {}