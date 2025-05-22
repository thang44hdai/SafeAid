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
    @SerialName("user_id") val userId: String,
    val username: String,
    val email: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("last_login") val lastLogin: String,
    @SerialName("profile_image_path") val profileImagePath: String?,
    val role: String
)