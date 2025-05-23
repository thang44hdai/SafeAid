package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("comment_id")   val commentId: String,
    @SerializedName("post_id")      val postId:    String,
    @SerializedName("user_id")      val userId:    String,
    @SerializedName("content")      val content:   String,
    @SerializedName("created_at")   val createdAt: String,
    @SerializedName("time_ago")   val timeAgo:   String,
    @SerializedName("user")         val user:      UserDto?
)

data class UserDto(
    @SerializedName("user_id")        val userId: String,
    @SerializedName("username")       val username: String,
    @SerializedName("profile_image_path") val avatarPath: String?
)

data class CommentListResponse(
    @SerializedName("comments") val comments: List<CommentDto>
)
