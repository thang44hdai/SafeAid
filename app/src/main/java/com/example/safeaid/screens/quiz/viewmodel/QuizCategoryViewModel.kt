package com.example.safeaid.screens.quiz.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.Category
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
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
            val response = apiService.getCategoryQuiz()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    updateState(DataResult.Success(QuizCategoryState.ListCategory(body.categories)))
                }
            } else {
            }
        }
    }

    override fun onTriggerEvent(event: QuizCategoryEvent) {
    }
}

sealed class QuizCategoryState {
    class ListCategory(val categories: List<Category>) : QuizCategoryState()
}

sealed class QuizCategoryEvent {}