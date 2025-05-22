package com.example.safeaid

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App() : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().subscribeToTopic("news")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    Log.d("hihihi", "Subscribed to 'news' topic thành công")
                } else {
//                    Log.e("hihihi", "Lỗi khi subscribe topic", task.exception)
                }
            }

    }
}