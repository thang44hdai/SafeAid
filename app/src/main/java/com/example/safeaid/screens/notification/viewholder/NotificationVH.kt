package com.example.safeaid.screens.notification.viewholder

import android.view.View
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