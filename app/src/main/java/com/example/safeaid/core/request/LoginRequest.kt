package com.example.safeaid.core.request

@kotlinx.serialization.Serializable
data class LoginRequest(
    val email: String,
    val password: String
)