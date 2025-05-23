package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class QuizIdRes (
    @SerializedName("quiz")
    var quiz: Quizze
)