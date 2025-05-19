package com.example.safeaid.core.utils

import android.os.Build
import android.util.Log
import com.example.androidtraining.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.HiltAndroidApp

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Lấy thông tin từ data payload (nếu gửi dạng `data`)
        val title = remoteMessage.data["title"] ?: "Tin tức mới"
        val content = remoteMessage.data["content"] ?: "Bạn có một tin tức mới"
        val newsId = remoteMessage.data["news_id"] ?: "fail Id"
        Log.i("hihihi", "receiver noti ${title} ${content} ${newsId}")
        // Tạo channel nếu cần
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createNotificationChannel(this)
        }

        // Gọi hiển thị thông báo
        NotificationUtils.showNewsNotification(
            context = this,
            title = title,
            content = content,
            newsId = newsId,
            icon = R.drawable.ic_noti_unread
        )
    }
}