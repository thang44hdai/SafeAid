package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class UserAnswer(
    @SerializedName("question_id")
    var questionId: String?,
    @SerializedName("quiz_attempt_id")
    var quizAttemptId: String?,
    @SerializedName("selected_answer_id")
    var selectedAnswerId: String?,
    @SerializedName("user_answer_id")
    var userAnswerId: String?,
    @SerializedName("user_id")
    var userId: String?
)