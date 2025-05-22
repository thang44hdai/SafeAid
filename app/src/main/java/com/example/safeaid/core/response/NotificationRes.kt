package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class NotificationRes(
    @SerializedName("data")
    var data: List<Notification>?
)

data class Notification(
    @SerializedName("content")
    var content: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("is_read")
    var isRead: Boolean?,
    @SerializedName("notification_id")
    var notificationId: String?,
    @SerializedName("ref_id")
    var refId: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("user_id")
    var userId: String?
)