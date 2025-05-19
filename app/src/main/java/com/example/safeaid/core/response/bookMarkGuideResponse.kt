package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class BookmarkResponse(
    @SerializedName("favlist_id")
    val favlistId: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("items")
    val items: List<BookmarkItem>
)

data class BookmarkItem(
    @SerializedName("guide_item_id")
    val guideItemId: String,

    @SerializedName("guide_id")
    val guideId: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("favlist_id")
    val favlistId: String,

    @SerializedName("guide")
    val guide: GuideBookMark
)

data class GuideBookMark(
    @SerializedName("guide_id")
    val guideId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("thumbnail_path")
    val thumbnailPath: String
)