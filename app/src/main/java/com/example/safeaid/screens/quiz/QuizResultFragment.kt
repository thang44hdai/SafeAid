package com.example.safeaid.screens.quiz

import android.util.Log
import androidx.fragment.app.activityViewModels
import com.example.androidtraining.databinding.FragmentQuizResultBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.request.AnswerRequest
import com.example.safeaid.core.request.QuizAttemptRequest
import com.example.safeaid.core.response.Question
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.viewmodel.QuizCategoryViewModel

class QuizResultFragment : BaseFragment<FragmentQuizResultBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: QuizCategoryViewModel by activityViewModels()

    var listQuestion: List<Question> = arrayListOf()
    var duration: Int = 0
    var time: String = ""
    var quiz: Quizze? = null
    override fun isHostFragment(): Boolean {
        return true
    }

    override fun onInit() {
        listQuestion = (arguments?.getSerializable("listQuestion") as? List<Question>) ?: listOf()
        duration = arguments?.getInt("duration") ?: 0
        time = arguments?.getString("time") ?: ""
        quiz = arguments?.getSerializable("quiz") as? Quizze
    }

    override fun onInitObserver() {
        viewBinding.btnFinish.setOnDebounceClick {
            mainNavigator.offerNavEvent(GoToMainScreen())
        }

        viewBinding.layoutBtnRedo.setOnDebounceClick {
            if (quiz != null)
                mainNavigator.offerNavEvent(GoToQuizFragment(quiz!!))
        }
    }

    override fun onInitListener() {
        viewBinding.tvDuration.text = duration.toString()
        viewBinding.tvTime.text = time
        viewBinding.tvQuizTitle.text = quiz?.title

        val numberQuestion = listQuestion.size
        val numberCorrectQuestion = listQuestion.sumOf { question ->
            question.answers.count { it.isSelected && it.isCorrect == 1 }
        }

        viewBinding.tvResult.text = "${numberCorrectQuestion}/${numberQuestion}"
        viewBinding.tvResultCount.text = numberCorrectQuestion.toString()

        val request = QuizAttemptRequest(
            quizId = quiz?.quizId ?: "",
            quizContent = quiz?.title ?: "",
            score = numberCorrectQuestion,
            maxScore = numberQuestion,
            duration = duration,
            completedAt = time,
            answers = listQuestion.map {
                AnswerRequest(
                    questionId = it.questionId,
                    selectedAnswerId = it.answers.firstOrNull { it.isSelected }?.answerId ?: ""
                )
            }
        )

        viewModel.saveQuizResult(request, ::notificationResultQuiz)
    }

    private fun notificationResultQuiz() {

    }
}

class GoToMainScreen() : BaseContainerFragment.NavigationEvent()