package com.example.safeaid.core.response

import com.example.safeaid.screens.community.data.PostDto

data class CreatePostResponse(
    val message: String,
    val post: PostDto  // bạn có thể tái sử dụng PostDto nếu trường hợp giống
)