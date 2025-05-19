package com.example.safeaid.core.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("phone_number")
    val phoneNumber: String
)