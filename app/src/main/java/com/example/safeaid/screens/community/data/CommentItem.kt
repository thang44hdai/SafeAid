package com.example.safeaid.screens.community.data

data class CommentItem(
    val userName: String,
    val time: String,
    val content: String,
    val profileImagePath: String? = null
)
