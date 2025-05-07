package com.example.safeaid.core.request


import com.google.gson.annotations.SerializedName

data class QuizAttemptRequest(
    @SerializedName("answers")
    val answers: List<AnswerRequest>,
    @SerializedName("completed_at")
    val completedAt: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("max_score")
    val maxScore: Int,
    @SerializedName("quiz_id")
    val quizId: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("user_id")
    val userId: String
)

data class AnswerRequest(
    @SerializedName("question_id")
    val questionId: String,
    @SerializedName("selected_answer_id")
    val selectedAnswerId: String
)