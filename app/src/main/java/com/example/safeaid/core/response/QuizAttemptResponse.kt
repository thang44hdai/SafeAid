package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class QuizAttemptResponse(
    @SerializedName("quizAttempts")
    var quizAttempts: List<QuizAttempt>?
) : Serializable {
    fun sortQuizAttemptsByCompletedAt() {
        quizAttempts = quizAttempts?.sortedByDescending { attempt ->
            attempt.completedAt?.let {
                val formatter =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                formatter.parse(it)
            }
        }
    }
}

data class QuizAttempt(
    @SerializedName("attempt_id")
    var attemptId: String?,
    @SerializedName("completed_at")
    var completedAt: String?,
    @SerializedName("duration")
    var duration: Int?,
    @SerializedName("max_score")
    var maxScore: Int?,
    @SerializedName("quiz")
    var quiz: Quizze?,
    @SerializedName("quiz_id")
    var quizId: String?,
    @SerializedName("score")
    var score: Int?,
    @SerializedName("user_id")
    var userId: String?
) : Serializable