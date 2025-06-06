package com.example.safeaid.core.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.safeaid.MainActivity
import com.example.safeaid.screens.news.NewsDetailsActivity
import kotlin.random.Random


object NotificationUtils {
    private const val CHANNEL_ID = "safe-aid"
    private const val CHANNEL_NAME = "safe-aid"

    @SuppressLint("MissingPermission")
    fun showNotification(context: Context, title: String, content: String, @DrawableRes icon: Int) {

        val activityClassName = "com.example.safeaid.MainActivity"
        val activityClass = Class.forName(activityClassName)
        val intent = Intent(context, activityClass).apply {
            flags = FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        val id = Random(System.currentTimeMillis()).nextInt(1000)
        notificationManager.notify(id, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false

        for (processInfo in runningAppProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && processInfo.processName == context.packageName
            ) {
                return true
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    fun showExamNotification(
        context: Context,
        title: String,
        content: String,
        refId: String,
        @DrawableRes icon: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to_quiz_detail", true)
            putExtra("ref_id", refId)
            putExtra("title", title)
            putExtra("content", content)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        val id = Random(System.currentTimeMillis()).nextInt(1000)
        notificationManager.notify(id, builder.build())
    }

    @SuppressLint("MissingPermission")
    fun showNewsNotification(
        context: Context,
        title: String,
        content: String,
        refId: String, // refId dùng để lấy chi tiết bài viết nếu cần
        htmlContent: String,
        @DrawableRes icon: Int
    ) {
        val intent = Intent(context, NewsDetailsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("news_title", title)
            putExtra("news_html", htmlContent)
            putExtra("ref_id", refId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1, // ID khác với exam để phân biệt
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        val id = Random(System.currentTimeMillis()).nextInt(1000)
        notificationManager.notify(id, builder.build())
    }



}