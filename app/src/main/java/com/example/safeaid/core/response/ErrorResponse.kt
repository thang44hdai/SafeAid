package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error")
    val error: String = "Lỗi không xác định"
) 