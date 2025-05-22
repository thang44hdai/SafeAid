package com.example.safeaid.screens.notification.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.Notification
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.Prefs.getToken
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.screens.quiz_history.data.QuizHistory
import com.example.safeaid.screens.quiz_history.viewmodel.QuizHistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.ClassCastException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : BaseViewModel<NotificationState, NotificationEvent>() {

    fun getNotification() {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getNotifications("Bearer ${getToken(context)}") },
                callback = { result ->
                    result.doIfSuccess {
                        updateState(
                            DataResult.Success(
                                NotificationState.NotificationList(
                                    it.data ?: listOf()
                                )
                            )
                        )
                    }
                }
            )
        }
    }

    fun getHistoryQuiz(quizAttemptId: String?, callback: (item: QuizAttempt) -> Unit) {
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

                        historyData.sortQuizAttemptsByCompletedAt()
                        val quizFilterItems: List<QuizHistory> = historyData.quizAttempts?.map {
                            val cate =
                                categoryData.categories.firstOrNull { it1 -> it1.quizzes.any { it2 -> it2.quizId == it.quizId } }

                            QuizHistory(quizAttempt = it, category = cate)
                        } ?: listOf()

                        val dataToDirect =
                            quizFilterItems.firstOrNull { it.quizAttempt.attemptId == quizAttemptId }

                        if (dataToDirect != null) {
                            callback.invoke(dataToDirect.quizAttempt)
                        }
                    } else {

                    }
                }
            } catch (e: SocketTimeoutException) {
            } catch (e: Exception) {
            }
        }
    }

    override fun onTriggerEvent(event: NotificationEvent) {
    }

}

sealed class NotificationState {
    class NotificationList(val data: List<Notification>) : NotificationState()
    class Quiz(val data: Quizze) : NotificationState()
}

sealed class NotificationEvent()