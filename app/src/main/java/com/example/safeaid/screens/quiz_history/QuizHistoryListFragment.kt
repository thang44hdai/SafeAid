package com.example.safeaid.screens.quiz_history

import android.app.DatePickerDialog
import android.util.Log
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentQuizHistoryBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.data.FilterCriteria
import com.example.safeaid.screens.quiz.data.QuizStatus
import com.example.safeaid.screens.quiz.viewholder.QuizHistoryVH
import com.example.safeaid.screens.quiz.viewmodel.QuizCategoryState
import com.example.safeaid.screens.quiz_history.data.QuizHistory
import com.example.safeaid.screens.quiz_history.viewholder.LayoutQuizHistoryVH
import com.example.safeaid.screens.quiz_history.viewmodel.QuizHistoryState
import com.example.safeaid.screens.quiz_history.viewmodel.QuizHistoryViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class QuizHistoryListFragment : BaseFragment<FragmentQuizHistoryBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: QuizHistoryViewModel by activityViewModels()
    private var currentFilter = FilterCriteria()
    private var listQuizAttempt: MutableList<QuizHistory> = mutableListOf()

    val adapter = adapterOf<QuizHistory> {
        diff(
            areItemsTheSame = { old, new -> old.quizAttempt.attemptId == new.quizAttempt.attemptId },
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
        currentFilter = viewModel.currentFilter
        viewModel.getHistoryQuiz()
        viewBinding.layoutBottomFilter.spinnerDuration.setItems(
            listOf(
                "No",
                "0-30 phút",
                "30 phút - 60 phút",
                "60 phút+"
            )
        )
        viewBinding.layoutBottomFilter.spinnerScore.setItems(
            listOf(
                "No",
                "0-5 câu",
                "5-10 câu",
                "10-20 câu",
                "20-50 câu"
            )
        )
    }

    override fun onInitObserver() {
        viewModel.viewState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> updateUi(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewBinding.tvSearch.addTextChangedListener {
            currentFilter = currentFilter.copy(keyword = it.toString())
            applyFilters()
        }
    }

    override fun onInitListener() {
        viewBinding.icBack.setOnDebounceClick {
            mainNavigator.offerNavEvent(PopBackStack())
            viewModel.currentFilter = FilterCriteria()
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
        viewBinding.layoutBottomFilter.spinnerContent.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            currentFilter = currentFilter.copy(category = text)
            applyFilters()
        }

        viewBinding.layoutBottomFilter.spinnerDuration.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            currentFilter = currentFilter.copy(duration = text)
            applyFilters()
        }

        viewBinding.layoutBottomFilter.spinnerScore.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            currentFilter = currentFilter.copy(score = text)
            applyFilters()
        }

        viewBinding.layoutBottomFilter.tvFromDate.setOnClickListener {
            showDatePicker(currentFilter.fromDate) { selectedDate ->
                viewBinding.layoutBottomFilter.tvFromDate.text = selectedDate
                currentFilter = currentFilter.copy(fromDate = selectedDate)
                applyFilters()
            }
        }

        viewBinding.layoutBottomFilter.tvToDate.setOnClickListener {
            showDatePicker(currentFilter.toDate) { selectedDate ->
                viewBinding.layoutBottomFilter.tvToDate.text = selectedDate
                currentFilter = currentFilter.copy(toDate = selectedDate)
                applyFilters()
            }
        }

        viewBinding.layoutBottomFilter.icReload.setOnDebounceClick {
            currentFilter = currentFilter.copy(fromDate = null, toDate = null)
            viewBinding.layoutBottomFilter.tvFromDate.text = "Từ ngày"
            viewBinding.layoutBottomFilter.tvToDate.text = "Đến ngày"
            applyFilters()
        }
    }

    private fun updateUi(state: DataResult<QuizHistoryState>?) {
        state?.doIfSuccess { data ->
            when (data) {
                is QuizHistoryState.HistoryQuestions -> {
                    listQuizAttempt = data.quizzes as MutableList<QuizHistory>
                    val cateList = viewModel.listCategory.map { it.name }.toMutableList()
                    cateList.add(0, "No")
                    viewBinding.layoutBottomFilter.spinnerContent.setItems(cateList)

                    adapter.submitList(data.quizzes)
                    applyFilters()
                }

                else -> {}
            }
        }
        state?.doIfFailure { }

    }

    private fun onClickQuizHistoryItem(item: QuizAttempt) {
        mainNavigator.offerNavEvent(GoToQuizHistoryDetail(item))
    }

    private fun showDatePicker(
        preSelectedDate: String?,
        onDateSelected: (String) -> Unit
    ) {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // Nếu có ngày đã chọn trước đó thì set lại Calendar
        preSelectedDate?.let {
            runCatching {
                format.parse(it)
            }.getOrNull()?.let { date ->
                calendar.time = date
            }
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    private fun applyFilters() {
        viewModel.currentFilter = currentFilter
        val filtered = listQuizAttempt.filter { item ->
            val matchesKeyword =
                item.quizAttempt.quiz?.title?.contains(currentFilter.keyword, ignoreCase = true)

            val matchesCategory = currentFilter.category?.let {
                item.category?.name == it || it == "No"
            } ?: true

            val matchesDuration = currentFilter.duration?.let {
                checkDuration(item.quizAttempt.quiz?.duration, it)
            } ?: true

            val matchesScore = currentFilter.score?.let {
                checkScore(item.quizAttempt?.score, it)
            } ?: true

            val matchesDateRange =
                checkDateRange(
                    item.quizAttempt?.completedAt,
                    currentFilter.fromDate,
                    currentFilter.toDate
                )

            matchesKeyword == true && matchesCategory && matchesDuration && matchesScore && matchesDateRange
        }

        adapter.submitList(filtered)
    }

    private fun checkDuration(duration: Int?, filter: String): Boolean {
        if (duration == null)
            return true

        if (filter == "No")
            return true
        return when (filter) {
            "0-30 phút" -> duration <= 30 * 60
            "30 phút - 60 phút" -> duration in 31 * 60..60 * 60
            "60 phút+" -> duration > 60 * 60
            else -> true
        }
    }

    private fun checkScore(score: Int?, filter: String): Boolean {
        if (score == null || filter == "No")
            return true

        return when (filter) {
            "0-5 câu" -> score <= 5
            "5-10 câu" -> score in 6..10
            "10-20 câu" -> score in 11..20
            "20-50 câu" -> score in 21..50
            else -> true
        }
    }

    private fun checkDateRange(date: String?, from: String?, to: String?): Boolean {
        if (date == null) return true

        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")

        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val quizDate = runCatching { isoFormat.parse(date) }.getOrNull() ?: return true
        val fromDate = from?.let { runCatching { displayFormat.parse(it) }.getOrNull() }
        val toDate = to?.let { runCatching { displayFormat.parse(it) }.getOrNull() }

        return (fromDate == null || !quizDate.before(fromDate)) &&
                (toDate == null || !quizDate.after(toDate))
    }


}

class GoToQuizHistoryDetail(val item: QuizAttempt) : BaseContainerFragment.NavigationEvent()