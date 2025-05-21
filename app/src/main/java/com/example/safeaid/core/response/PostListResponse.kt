package com.example.safeaid.core.response

import com.example.safeaid.screens.community.data.PostDto
import com.google.gson.annotations.SerializedName

data class PostListResponse(
    val page: Int,
    val limit: Int,
    @SerializedName("posts") val posts: List<PostDto>
)
