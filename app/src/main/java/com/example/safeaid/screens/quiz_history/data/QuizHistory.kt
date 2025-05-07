package com.example.safeaid.screens.quiz_history.data

import com.example.safeaid.core.response.Category
import com.example.safeaid.core.response.QuizAttempt

data class QuizHistory(
    var quizAttempt: QuizAttempt,
    var category: Category?
)