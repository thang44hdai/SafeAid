package com.example.safeaid.screens.quiz.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.Category
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
    fun getQuizCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getCategoryQuiz() },
                callback = { result ->
                    result.doIfSuccess {
                        updateState(DataResult.Success(QuizCategoryState.ListCategory(it.categories)))
                    }
                    result.doIfFailure {
                        Log.i("hihihi", "$it")
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
}

sealed class QuizCategoryEvent {}