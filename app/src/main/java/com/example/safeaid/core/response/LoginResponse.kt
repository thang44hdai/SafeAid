package com.example.safeaid.core.response

@kotlinx.serialization.Serializable
data class LoginResponse(
    val message: String,
    val token: String
)