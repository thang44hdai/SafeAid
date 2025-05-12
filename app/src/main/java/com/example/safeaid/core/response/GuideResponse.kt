package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class GuideResponse(
    @SerializedName("page")
    val page: Int = 0,
    
    @SerializedName("limit")
    val limit: Int = 0,
    
    @SerializedName("guides")
    val guides: List<Guide> = emptyList()
)

data class Guide(
    @SerializedName("guide_id")
    val guideId: String = "",
    
    @SerializedName("title")
    val title: String = "",
    
    @SerializedName("description")
    val description: String = "",
    
    @SerializedName("thumbnail_path")
    val thumbnailPath: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String = "",
    
    @SerializedName("updated_at")
    val updatedAt: String = "",
    
    @SerializedName("view_count")
    val viewCount: Int = 0,
    
    @SerializedName("category_id")
    val categoryId: String = "",
    
    @SerializedName("category")
    val category: CategoryInfo = CategoryInfo(),
    
    @SerializedName("media")
    val media: List<MediaResponse> = emptyList()
)

data class CategoryInfo(
    @SerializedName("category_id")
    val categoryId: String = "",
    
    @SerializedName("name")
    val name: String = ""
)

data class MediaResponse(
    @SerializedName("media_id")
    val mediaId: String = "",

    @SerializedName("media_type")
    val mediaType: String = "",

    @SerializedName("media_url")
    val mediaUrl: String = "",

    @SerializedName("caption")
    val caption: String? = null
)