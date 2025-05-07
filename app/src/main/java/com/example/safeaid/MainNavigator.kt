package com.example.safeaid

import androidx.lifecycle.ViewModel
import com.example.safeaid.core.ui.BaseContainerFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import javax.inject.Inject

@HiltViewModel
class MainNavigator @Inject constructor(
) : ViewModel() {
    protected val _navigation = Channel<BaseContainerFragment.NavigationEvent>(Channel.UNLIMITED)
    val navigation: ReceiveChannel<BaseContainerFragment.NavigationEvent> get() = _navigation
    fun offerNavEvent(event: BaseContainerFragment.NavigationEvent) {
        _navigation.trySend(event)
    }

    suspend fun sendNavEvent(event: BaseContainerFragment.NavigationEvent) {
        _navigation.send(event)
    }
}