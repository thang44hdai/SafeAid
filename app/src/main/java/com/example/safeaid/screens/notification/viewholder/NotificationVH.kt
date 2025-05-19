package com.example.safeaid.screens.notification.viewholder

import android.view.View
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ItemNotificationBinding
import com.example.safeaid.core.response.Notification
import com.example.safeaid.core.ui.recyclerview.core.RecyclerViewHolder
import com.example.safeaid.core.utils.setOnDebounceClick

class NotificationVH(view: View, private val callback: (item: Notification) -> Unit?) :
    RecyclerViewHolder<Notification>(view) {
    private val viewBinding = ItemNotificationBinding.bind(view)

    override fun bind(position: Int, item: Notification) {
        viewBinding.tvTitle.text = item.title
        viewBinding.tvContent.text = item.content

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

}