package com.example.safeaid.screens.quiz_history

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentQuizHistoryDetailBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.response.Answer
import com.example.safeaid.core.response.Question
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.ImageZoomDialog
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz_history.viewholder.AnswerHistoryVH
import com.example.safeaid.screens.quiz_history.viewholder.QuestionHistoryVH
import com.example.safeaid.screens.quiz_history.viewmodel.QuizHistoryState
import com.example.safeaid.screens.quiz_history.viewmodel.QuizHistoryViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class QuizHistoryDetailFragment : BaseFragment<FragmentQuizHistoryDetailBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private var quizAttempt: QuizAttempt? = null
    private val viewModel: QuizHistoryViewModel by activityViewModels()
    private var listQuestion: MutableList<Question> = mutableListOf()

    private val questionAdapter = adapterOf<Question> {
        diff(
            areItemsTheSame = { old, new -> old.questionId == new.questionId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.layout_question_item,
            viewHolder = { view -> QuestionHistoryVH(view, ::onClickQuestion) }
        )
    }

    private val answerAdapter = adapterOf<Answer> {
        diff(
            areItemsTheSame = { old, new -> old.answerId == new.answerId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.answer_item,
            viewHolder = { view -> AnswerHistoryVH(view, ::getSelectedAnswerId) }
        )
    }

    override fun isHostFragment(): Boolean {
        return false
    }

    override fun onInit() {
        quizAttempt = arguments?.getSerializable("quizAttempt") as QuizAttempt
        viewBinding.rcvQuestion.adapter = questionAdapter
        viewBinding.rcvAnswer.adapter = answerAdapter
        viewModel.getHistoryDetail(
            quizAttemptId = quizAttempt?.attemptId ?: "",
            quizId = quizAttempt?.quizId ?: ""
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
    }

    private fun updateUi(state: DataResult<QuizHistoryState>?) {
        state?.doIfSuccess { data ->
            when (data) {
                is QuizHistoryState.HistoryDetail -> {
                    listQuestion = data.data as MutableList<Question>
                    questionAdapter.submitList(data.data)
                    if (listQuestion.size > 0) {
                        onClickQuestion(listQuestion[0])
                    }
                }

                else -> {}
            }
        }
        state?.doIfFailure { }

    }

    private fun onClickQuestion(question: Question) {
        listQuestion = listQuestion.map {
            it.copy(isSelected = question.questionId == it.questionId)
        } as MutableList<Question>

        if (question.imageUrl == null) {
            viewBinding.imv.isVisible = false
        } else {
            viewBinding.imv.isVisible = true
            Glide.with(viewBinding.imv.context)
                .load(question.imageUrl)
                .into(viewBinding.imv)
        }

        viewBinding.tvTitle.text = question.content
        questionAdapter.submitList(listQuestion)
        answerAdapter.submitList(question.answers)
        viewBinding.imv.setOnClickListener {
            question.imageUrl?.let { it1 ->
                val dialog = ImageZoomDialog(question.imageUrl)
                dialog.show(parentFragmentManager, "zoom")
            }
        }
    }

    private fun getSelectedAnswerId(item: Answer): String? {
        val ques =
            listQuestion.firstOrNull { it.answers.any { ase -> ase.answerId == item.answerId } }

        return ques?.selectedAnswerId
    }

}