package com.example.safeaid.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.time.ExperimentalTime

// Trả về theo dạng (15p trước , 2h trước, 1 ngày trước, 1 tháng trước, 1 năm trước)
fun convertTime (time: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date: Date = inputFormat.parse(time) ?: return ""

    val currentTime = System.currentTimeMillis()
    val diffInMillis = currentTime - date.time

    val seconds = diffInMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val months = days / 30
    val years = months / 12

    return when {
        seconds < 60 -> "$seconds giây trước"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        days < 30 -> "$days ngày trước"
        months < 12 -> "$months tháng trước"
        else -> "$years năm trước"
    }
}