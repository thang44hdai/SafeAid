package com.example.safeaid.core.request

@kotlinx.serialization.Serializable
class EditProfileRequest {
    var username: String? = null
    var phone_number: String? = null
}