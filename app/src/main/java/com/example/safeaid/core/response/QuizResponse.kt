package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuizResponse(
    @SerializedName("questions")
    val questions: List<Question>
) : Serializable

data class Question(
    @SerializedName("answers")
    val answers: List<Answer>,
    @SerializedName("content")
    val content: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("point")
    val point: Int,
    @SerializedName("question_id")
    val questionId: String,
    @SerializedName("quiz_id")
    val quizId: String,
    var isSelected: Boolean = false,
    var isFlag: Boolean = false,
    var selectedAnswerId: String? = null
) : Serializable {
    fun isAnswered(): Boolean {
        return answers.any { it.isSelected }
    }
}

data class Answer(
    @SerializedName("answer_id")
    val answerId: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("is_correct")
    val isCorrect: Int,
    var isSelected: Boolean = false
) : Serializable