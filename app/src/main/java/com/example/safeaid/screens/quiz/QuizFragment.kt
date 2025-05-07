package com.example.safeaid.screens.quiz

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentQuizzBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.response.QuizResponse
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.data.ItemQuizCategory
import com.example.safeaid.screens.quiz.viewholder.QuizHistoryVH
import com.example.safeaid.screens.quiz.viewmodel.QuizCategoryState
import com.example.safeaid.screens.quiz.viewmodel.QuizCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class QuizFragment : BaseFragment<FragmentQuizzBinding>() {
    private val viewModel: QuizCategoryViewModel by activityViewModels()
    private val mainNavigator: MainNavigator by activityViewModels()
    private lateinit var quiz: Quizze
    private var quizQuestion: QuizResponse = QuizResponse(listOf())

    private val quizHistoryAdapter = adapterOf<QuizAttempt> {
        diff(
            areItemsTheSame = { old, new -> old.attemptId == new.attemptId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.quiz_history_item,
            viewHolder = { view -> QuizHistoryVH(view, null) }
        )
    }

    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        quiz = arguments?.getSerializable("quizId") as Quizze
        if (quiz != null) {
            viewModel.getQuestionByQuiz(quiz.quizId)
            viewBinding.tv1.text = quiz.title
        }
        viewBinding.rcvDone.adapter = quizHistoryAdapter
        viewModel.getHistoryOfQuiz(quiz)
    }

    override fun onInitObserver() {
        viewModel.viewState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> updateUi(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onInitListener() {
        viewBinding.btnStart.setOnDebounceClick {
            mainNavigator.offerNavEvent(GoToDoQuizFragment(quiz, quizQuestion))
        }

        viewBinding.icBack.setOnDebounceClick {
            mainNavigator.offerNavEvent(PopBackStack())
        }
    }

    private fun updateUi(state: DataResult<QuizCategoryState>?) {
        state?.doIfSuccess { data ->
            when (data) {
                is QuizCategoryState.QuestionsByQuizId -> {
                    quizQuestion = data.questions
                    viewBinding.tv2.text = data.questions.questions.size.toString() + " câu hỏi"
                    viewBinding.countQuestion.text =
                        data.questions.questions.size.toString() + " câu hỏi"
                    viewBinding.tvTime.text =
                        "Thời gian làm bài là ${formatSecondsToTime(quiz.duration)}"
                }

                is QuizCategoryState.HistoryOfQuiz -> {
                    viewBinding.tvHistory.text = "Lịch sử (${data.quizzes.quizAttempts?.size ?: 0})"
                    quizHistoryAdapter.submitList(data.quizzes.quizAttempts)
                }

                else -> {}
            }
        }
        state?.doIfFailure { }

    }

    private fun formatSecondsToTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }
}

class GoToDoQuizFragment(val quiz: Quizze, val quizQuestion: QuizResponse) :
    BaseContainerFragment.NavigationEvent()