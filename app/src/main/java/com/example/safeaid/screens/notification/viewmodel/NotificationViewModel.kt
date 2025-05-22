package com.example.safeaid.screens.notification.viewmodel

import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel<NotificationState, NotificationEvent>() {

    fun getNotification() {

    }

    override fun onTriggerEvent(event: NotificationEvent) {
    }

}

sealed class NotificationState()
sealed class NotificationEvent()