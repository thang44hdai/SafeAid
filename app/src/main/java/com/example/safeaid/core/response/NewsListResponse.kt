package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class NewsListResponse(
    val message: String,
    val data: List<NewsDto>
)

data class NewsDto(
    @SerializedName("news_id")
    val id: String,
    val title: String,
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("view_count")
    val viewCount: Int,
    // nếu API trả thumbnail_path, bạn cũng map ở đây
    @SerializedName("thumbnail_url")
    val thumbnail: String?,
    @SerializedName("media")
    val media: List<MediaDto>? = null
)

data class MediaDto(
    @SerializedName("media_id")
    val id: String,
    @SerializedName("media_url")
    val url: String,
    @SerializedName("media_type")
    val type: String,
    @SerializedName("caption")
    val caption: String? = null,
)
