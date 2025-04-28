package com.example.safeaid.screens.quiz

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.databinding.FragmentQuizzBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.response.QuizResponse
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.data.ItemQuizCategory
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
    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        quiz = arguments?.getSerializable("quizId") as Quizze
        if (quiz != null) {
            viewModel.getQuestionByQuiz(quiz.quizId)
            viewBinding.tv1.text = quiz.title
        }
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
                    viewBinding.tvTime.text = "Thời gian làm bài là ${quiz.duration}"
                }

                else -> {}
            }
        }
        state?.doIfFailure { }

    }
}

class GoToDoQuizFragment(val quiz: Quizze, val quizQuestion: QuizResponse) : BaseContainerFragment.NavigationEvent()