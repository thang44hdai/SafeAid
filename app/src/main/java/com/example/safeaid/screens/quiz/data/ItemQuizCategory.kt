package com.example.safeaid.screens.quiz.data

import com.example.safeaid.core.response.Category
import java.util.UUID

data class ItemQuizCategory(
    var id: String = UUID.randomUUID().toString(),
    var quizCategory: Category,
    var isSelected : Boolean
)
