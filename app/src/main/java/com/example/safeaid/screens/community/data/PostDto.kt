package com.example.safeaid.screens.community.data

import com.google.gson.annotations.SerializedName

data class PostDto(
    val post_id: String,
    val user_id: String,
    val title: String,
    val content: String,
    val created_at: String,
    val updated_at: String,
    val like_count: Int,
    val comment_count: Int,
    val view_count: Int,
    val user: UserDto,
    val media: List<PostMediaDto>
)
