package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class GuideStepMediaResponse(
    @SerializedName("media_id")
    val mediaId: String = "",

    @SerializedName("guide_id")
    val guideId: String = "",

    @SerializedName("media_type")
    val mediaType: String = "",

    @SerializedName("media_url")
    val mediaURL: String = "",

    @SerializedName("created_at")
    val createdAt: String = "",

    @SerializedName("order_index")
    val orderIndex: String = "",

    @SerializedName("caption")
    val caption: String = ""
)