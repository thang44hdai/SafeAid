package com.example.safeaid.screens.notification

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.databinding.FragmentNotificationBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.notification.viewmodel.NotificationState
import com.example.safeaid.screens.notification.viewmodel.NotificationViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: NotificationViewModel by activityViewModels()
    override fun isHostFragment(): Boolean {
        return false
    }

    override fun onInit() {
        viewModel.getNotification()
        viewBinding.dropdownMenu.setItems(
            listOf(
                "Toàn bộ",
                "Chưa đọc",
                "Đã đọc"
            )
        )
    }

    override fun onInitObserver() {
        viewModel.viewState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> updateUi(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onInitListener() {
        viewBinding.icBack.setOnDebounceClick {
            mainNavigator.offerNavEvent(PopBackStack())
        }

        viewBinding.dropdownMenu.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            Log.i("hihihi", "${text}")
        }
    }

    private fun updateUi(state: DataResult<NotificationState>?) {
        state?.doIfSuccess { data ->
            when (data) {
                else -> {}
            }
        }
        state?.doIfFailure { }

    }

}