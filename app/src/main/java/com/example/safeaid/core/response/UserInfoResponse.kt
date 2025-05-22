package com.example.safeaid.core.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val message: String,
    val user: UserInfo
)

@Serializable
data class UserInfo(
    val user_id: String,
    val username: String,
    val email: String,
    val phone_number: String,
    val created_at: String,
    val updated_at: String,
    val last_login: String,
    val profile_image_path: String?,
    val role: String
)