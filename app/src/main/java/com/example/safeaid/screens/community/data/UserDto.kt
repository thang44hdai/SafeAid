package com.example.safeaid.screens.community.data

import com.google.gson.annotations.SerializedName

data class UserDto(
    val user_id: String,
    val username: String,
    val profile_image_path: String?
)
