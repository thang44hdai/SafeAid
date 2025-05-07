package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName


data class QuizHistoryDetailResponse(
    @SerializedName("questions")
    var questions: List<Question>?,
    @SerializedName("user_answers")
    var userAnswers: List<UserAnswer>?
)