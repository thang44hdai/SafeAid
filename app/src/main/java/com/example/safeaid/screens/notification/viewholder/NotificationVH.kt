package com.example.safeaid.screens.notification.viewholder

import android.view.View
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ItemNotificationBinding
import com.example.safeaid.core.response.Notification
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class NotificationVH(view: View, private val callback: (item: Notification) -> Unit?) :
    RecyclerViewHolder<Notification>(view) {
    private val viewBinding = ItemNotificationBinding.bind(view)

    override fun bind(position: Int, item: Notification) {
        viewBinding.tvTitle.text = item.title
        viewBinding.tvContent.text = item.content
        viewBinding.tvTime.text = getTimeAgo(item.createdAt ?: "")

        if (item.type == "exam") {
            Glide.with(viewBinding.imv.context)
                .load("https://st5.depositphotos.com/1915171/66288/v/450/depositphotos_662887866-stock-illustration-quiz-tag-speech-bubble-banner.jpg")
                .into(viewBinding.imv)
        } else {
            Glide.with(viewBinding.imv.context)
                .load("https://png.pngtree.com/png-clipart/20190705/original/pngtree-vector-creative-hot-news-banner-png-image_4275281.jpg")
                .into(viewBinding.imv)
        }

        if (item.isRead == true) {
            viewBinding.parent.setBackgroundResource(0)
        } else {
            viewBinding.parent.setBackgroundResource(R.drawable.bg_noti_unread)
        }
        viewBinding.root.setOnDebounceClick {
            callback.invoke(item)
        }
    }

    fun getTimeAgo(isoTime: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val time = sdf.parse(isoTime)?.time ?: return ""

            val now = System.currentTimeMillis()
            val diff = now - time

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            when {
                seconds < 60 -> "Vừa xong"
                minutes < 60 -> "$minutes phút trước"
                hours < 24 -> "$hours giờ trước"
                days == 1L -> "Hôm qua"
                days < 7 -> "$days ngày trước"
                else -> {
                    // Nếu quá 1 tuần thì hiển thị ngày cụ thể
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    outputFormat.timeZone = TimeZone.getDefault()
                    outputFormat.format(Date(time))
                }
            }
        } catch (e: Exception) {
            ""
        }
    }


}