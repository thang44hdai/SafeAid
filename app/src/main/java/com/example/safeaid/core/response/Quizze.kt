package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Quizze(
    @SerializedName("description")
    val description: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("quiz_id")
    val quizId: String,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String?,
    @SerializedName("title")
    val title: String,
    @SerializedName("guide_id")
    val guideId: String?
) : Serializable
