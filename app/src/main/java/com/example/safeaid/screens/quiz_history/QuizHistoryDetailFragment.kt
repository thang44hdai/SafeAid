package com.example.safeaid.screens.quiz_history

import androidx.fragment.app.activityViewModels
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentQuizHistoryDetailBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.response.Answer
import com.example.safeaid.core.response.Question
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz_history.viewholder.AnswerHistoryVH
import com.example.safeaid.screens.quiz_history.viewholder.QuestionHistoryVH

class QuizHistoryDetailFragment : BaseFragment<FragmentQuizHistoryDetailBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private var quizAttempt: QuizAttempt? = null

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
            viewHolder = ::AnswerHistoryVH
        )
    }

    override fun isHostFragment(): Boolean {
        return false
    }

    override fun onInit() {
        quizAttempt = arguments?.getSerializable("quizAttempt") as QuizAttempt
        viewBinding.rcvQuestion.adapter = questionAdapter
        viewBinding.rcvAnswer.adapter = answerAdapter
    }

    override fun onInitObserver() {
    }

    override fun onInitListener() {
        viewBinding.icBack.setOnDebounceClick {
            mainNavigator.offerNavEvent(PopBackStack())
        }
    }

    private fun onClickQuestion(question: Question) {
//        listQuestion = listQuestion.map {
//            it.copy(isSelected = question.questionId == it.questionId)
//        } as MutableList<Question>
//
//        viewBinding.tvTitle.text = question.content
//        questionAdapter.submitList(listQuestion)
//        questionExpandAdapter.submitList(listQuestion)
//        answerApdater.submitList(question.answers)
    }

}