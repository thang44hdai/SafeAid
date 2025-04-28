package com.example.safeaid.screens.quiz

import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentTestQuizBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.core.response.Answer
import com.example.safeaid.core.response.Question
import com.example.safeaid.core.response.QuizResponse
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.ui.BaseDialog
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.NotificationUtils
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.viewholder.AnswerViewHolder
import com.example.safeaid.screens.quiz.viewholder.QuestionExpandVH
import com.example.safeaid.screens.quiz.viewholder.QuestionViewHolder
import com.example.safeaid.screens.quiz.viewmodel.QuizCategoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TestQuizFragment : BaseFragment<FragmentTestQuizBinding>() {
    private val viewModel: QuizCategoryViewModel by activityViewModels()
    private val mainNavigator: MainNavigator by activityViewModels()

    private lateinit var quiz: Quizze
    private var quizQuestion: QuizResponse = QuizResponse(listOf())
    private var listQuestion: MutableList<Question> = mutableListOf()

    private var countDownTimer: CountDownTimer? = null

    private val questionAdapter = adapterOf<Question> {
        diff(
            areItemsTheSame = { old, new -> old.questionId == new.questionId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.layout_question_item,
            viewHolder = { view -> QuestionViewHolder(view, ::onClickQuestion) }
        )
    }

    private val questionExpandAdapter = adapterOf<Question> {
        diff(
            areItemsTheSame = { old, new -> old.questionId == new.questionId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.layout_question_item,
            viewHolder = { view -> QuestionExpandVH(view, ::onClickQuestion) }
        )
    }

    private val answerApdater = adapterOf<Answer> {
        diff(
            areItemsTheSame = { old, new -> old.answerId == new.answerId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.answer_item,
            viewHolder = { view -> AnswerViewHolder(view, ::onClickAnswer) }
        )
    }

    // them chuc nang dat co, hien thi cau da lam, mo rong hien thi cau hoi

    override fun isHostFragment(): Boolean {
        return true
    }

    override fun onInit() {
        quiz = arguments?.getSerializable("quiz") as Quizze
        quizQuestion = arguments?.getSerializable("questions") as QuizResponse

        if (quizQuestion != null) {
            listQuestion = quizQuestion.questions as MutableList<Question>
            if (listQuestion.size > 0) {
                onClickQuestion(listQuestion[0])
            }
            viewBinding.tv1.text = quiz.title
            startCountdown(quiz.duration)
        }
//        listQuestion = MutableList(15) { index ->  // Tạo 5 câu hỏi
//            Question(
//                answers = List(4) { i ->  // Mỗi câu có 4 đáp án
//                    Answer(
//                        answerId = "ans_${index}_$i",
//                        content = "Đáp án ${i + 1} cho câu hỏi ${index + 1}",
//                        isCorrect = if (i == 0) 1 else 0  // Đáp án đầu tiên đúng
//                    )
//                },
//                content = "Nội dung câu hỏi số ${index + 1}",
//                imageUrl = null, // Có thể thay thành URL giả nếu cần
//                point = 10,
//                questionId = "ques_$index",
//                quizId = "quiz_001",
//                isSelected = index == 0
//            )
//        }
//        onClickQuestion(listQuestion[0])

        viewBinding.rcvQuestion.adapter = questionAdapter
        viewBinding.rcvExpand.adapter = questionExpandAdapter
        viewBinding.rcvAnswer.adapter = answerApdater
        questionAdapter.submitList(listQuestion)
        questionExpandAdapter.submitList(listQuestion)
    }

    override fun onInitObserver() {
    }

    override fun onInitListener() {
        viewBinding.btnEnd.setOnDebounceClick {
            val dialog = BaseDialog(viewBinding.root.context)
            dialog.setView(
                title = "",
                message = "Bạn có chắc muốn nộp bài?",
                onClickPositive = ::onClickSubmitTest,
                onClickNegative = null
            )
            dialog.show()
        }

        viewBinding.icFlag.setOnDebounceClick {
            listQuestion = listQuestion.map {
                if (it.isSelected) {
                    it.copy(isFlag = !it.isFlag)
                } else it.copy()
            } as MutableList<Question>

            questionAdapter.submitList(listQuestion)
            questionExpandAdapter.submitList(listQuestion)
        }

        viewBinding.icExpand.setOnClickListener {
            viewBinding.drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun onClickSubmitTest() {
        countDownTimer?.cancel()
        mainNavigator.offerNavEvent(
            OnSubmitTest(
                listQuestion as ArrayList<Question>,
                quiz.duration - parseTimeToSeconds(viewBinding.tvTime.text.toString()),
                getCurrentTimestamp(),
                quiz
            )
        )

        NotificationUtils.showNotification(
            requireContext(),
            "Bài làm đã được nộp",
            "Chúc mừng bạn đã hoàn thành bài thi ${quiz.title}",
            R.drawable.ic_noti_unread
        )
    }

    private fun onClickQuestion(question: Question) {
        listQuestion = listQuestion.map {
            it.copy(isSelected = question.questionId == it.questionId)
        } as MutableList<Question>

        viewBinding.tvTitle.text = question.content
        questionAdapter.submitList(listQuestion)
        questionExpandAdapter.submitList(listQuestion)
        answerApdater.submitList(question.answers)
    }

    private fun onClickAnswer(answer: Answer) {
        val question = listQuestion.find { it.isSelected }
        if (question != null) {
            var answers = question.answers
            answers = answers.map {
                it.copy(isSelected = it.answerId == answer.answerId)
            }

            answerApdater.submitList(answers)

            listQuestion = listQuestion.map {
                if (it.isSelected) {
                    it.copy(answers = answers)
                } else {
                    it.copy()
                }
            } as MutableList<Question>
            questionAdapter.submitList(listQuestion)
            questionExpandAdapter.submitList(listQuestion)
        }
    }

    private fun startCountdown(totalSeconds: Int) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(totalSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                viewBinding.tvTime.text = formatTime(secondsLeft)
            }

            override fun onFinish() {
                countDownTimer?.cancel()
                mainNavigator.offerNavEvent(
                    OnSubmitTest(
                        listQuestion as ArrayList<Question>,
                        quiz.duration - parseTimeToSeconds(viewBinding.tvTime.text.toString()),
                        getCurrentTimestamp(),
                        quiz
                    )
                )
                NotificationUtils.showNotification(
                    requireContext(),
                    "Bài làm đã được nộp",
                    "Chúc mừng bạn đã hoàn thành bài thi ${quiz.title}",
                    R.drawable.ic_noti_unread
                )
            }
        }.start()
    }

    private fun formatTime(seconds: Int): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hrs, mins, secs)
    }

    private fun parseTimeToSeconds(time: String): Int {
        val parts = time.split(":")
        if (parts.size != 3) return 0
        val hrs = parts[0].toIntOrNull() ?: 0
        val mins = parts[1].toIntOrNull() ?: 0
        val secs = parts[2].toIntOrNull() ?: 0
        return hrs * 3600 + mins * 60 + secs
    }

    fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

}

class OnSubmitTest(
    val listQuestion: ArrayList<Question>,
    val duration: Int,
    val time: String,
    val quiz: Quizze
) : BaseContainerFragment.NavigationEvent()