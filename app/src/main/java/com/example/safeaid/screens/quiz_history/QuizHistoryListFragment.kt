package com.example.safeaid.screens.quiz_history

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentQuizHistoryBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.viewholder.QuizHistoryVH
import com.example.safeaid.screens.quiz_history.viewholder.LayoutQuizHistoryVH
import com.example.safeaid.screens.quiz_history.viewmodel.QuizHistoryViewModel

class QuizHistoryListFragment : BaseFragment<FragmentQuizHistoryBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: QuizHistoryViewModel by activityViewModels()

    val adapter = adapterOf<QuizAttempt> {
        diff(
            areItemsTheSame = { old, new -> old.attemptId == new.attemptId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.layout_quiz_history_item,
            viewHolder = { view -> LayoutQuizHistoryVH(view, ::onClickQuizHistoryItem) }
        )
    }

    override fun isHostFragment(): Boolean {
        return true
    }

    override fun onInit() {
        viewBinding.layoutBottomFilter.layout1.isVisible = false
        viewBinding.layoutBottomFilter.layout3.isVisible = true
        viewBinding.layoutBottomFilter.layout4.isVisible = true
        viewBinding.rcvQuiz.adapter = adapter
        viewModel.getHistoryOfQuiz()
    }

    override fun onInitObserver() {
    }

    override fun onInitListener() {
        viewBinding.icBack.setOnDebounceClick {
            mainNavigator.offerNavEvent(PopBackStack())
        }

        viewBinding.layoutFilter.setOnDebounceClick {
            val filterView = viewBinding.layoutBottomFilter.root
            if (filterView.isVisible) {
                filterView.animate()
                    .alpha(0f)
                    .translationY(-20f)
                    .setDuration(200)
                    .withEndAction {
                        filterView.isVisible = false
                        filterView.alpha = 1f
                        filterView.translationY = 0f
                    }
                    .start()
            } else {
                filterView.alpha = 0f
                filterView.translationY = -20f
                filterView.isVisible = true
                filterView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(200)
                    .start()
            }
        }
    }

    private fun onClickQuizHistoryItem(item: QuizAttempt) {
        mainNavigator.offerNavEvent(GoToQuizHistoryDetail(item))
    }
}

class GoToQuizHistoryDetail(val item: QuizAttempt) : BaseContainerFragment.NavigationEvent()