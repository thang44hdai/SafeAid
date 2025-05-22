package com.example.safeaid.screens.notification

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentNotificationBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.response.Notification
import com.example.safeaid.core.response.Question
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.notification.viewholder.NotificationVH
import com.example.safeaid.screens.notification.viewmodel.NotificationState
import com.example.safeaid.screens.notification.viewmodel.NotificationViewModel
import com.example.safeaid.screens.quiz.GoToQuizFragment
import com.example.safeaid.screens.quiz_history.GoToQuizHistoryDetail
import com.example.safeaid.screens.quiz_history.viewholder.QuestionHistoryVH
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: NotificationViewModel by activityViewModels()

    private val adapter = adapterOf<Notification> {
        diff(
            areItemsTheSame = { old, new -> old.notificationId == new.notificationId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.item_notification,
            viewHolder = { view -> NotificationVH(view, ::onClickNotiItem) }
        )
    }

    override fun isHostFragment(): Boolean {
        return false
    }

    override fun onInit() {
        viewBinding.rcvNoti.adapter = adapter
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
                is NotificationState.NotificationList -> {
                    adapter.submitList(data.data)
                }

                else -> {}
            }
        }
        state?.doIfFailure { }

    }

    private fun onClickNotiItem(item: Notification) {
        if (item.type == "exam") {
            viewModel.getHistoryQuiz(item.refId) {
                mainNavigator.offerNavEvent(GoToQuizHistoryDetail(it))
            }
        } else if (item.title == "news") {
        } else if (item.title == "community") {
        }
    }

}