package com.example.safeaid.screens.quiz.data

import com.example.safeaid.core.response.Category
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.response.Quizze
import java.util.UUID

data class QuizFilterItem(
    var id: String = UUID.randomUUID().toString(),
    var quiz: Quizze,
    var quizAttempt: QuizAttempt?,
    var category: Category,
    var isDone: Boolean
)
