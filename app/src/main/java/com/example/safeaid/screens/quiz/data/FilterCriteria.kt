package com.example.safeaid.screens.quiz.data

data class FilterCriteria(
    val keyword: String = "",
    val status: QuizStatus = QuizStatus.ALL,
    val category: String? = null,
    val duration: String? = null,
    val score: String? = null,
    val fromDate: String? = null,
    val toDate: String? = null
)

enum class QuizStatus { ALL, DONE, NOT_DONE }
