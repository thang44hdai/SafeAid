package com.example.safeaid.screens.quiz

import android.app.DatePickerDialog
import android.util.Log
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentSearchQuizBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.data.FilterCriteria
import com.example.safeaid.screens.quiz.data.QuizFilterItem
import com.example.safeaid.screens.quiz.data.QuizStatus
import com.example.safeaid.screens.quiz.viewholder.FilterQuizItemVH
import com.example.safeaid.screens.quiz.viewholder.QuizHistoryVH
import com.example.safeaid.screens.quiz.viewmodel.SearchQuizState
import com.example.safeaid.screens.quiz.viewmodel.SearchQuizViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class SearchQuizFragment : BaseFragment<FragmentSearchQuizBinding>() {
    private val viewModel: SearchQuizViewModel by activityViewModels()
    private val mainNavigator: MainNavigator by activityViewModels()

    private var filterData: MutableList<QuizFilterItem> = mutableListOf()
    private var currentFilter = FilterCriteria()

    val adapter = adapterOf<QuizFilterItem> {
        diff(
            areItemsTheSame = { old, new -> old.id == new.id },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.layout_quiz_filter_item,
            viewHolder = { view -> FilterQuizItemVH(view, ::onClickQuiz) }
        )
    }


    override fun isHostFragment(): Boolean {
        return false
    }

    override fun onInit() {
        val key = arguments?.getString("search") as String

        currentFilter = currentFilter.copy(keyword = key)

        viewBinding.tvSearch.setText(key)
        viewBinding.rcvQuiz.adapter = adapter
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
        viewModel.getData()
        viewModel.getQuizCategory()
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

        viewBinding.tvSearch.addTextChangedListener {
            currentFilter = currentFilter.copy(keyword = it.toString())
            applyFilters()
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

        viewBinding.layoutBottomFilter.radioGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            val shouldShow = checkedId == R.id.radio_done
            var status = QuizStatus.ALL
            when (checkedId) {
                R.id.radio_done -> {
                    status = QuizStatus.DONE
                }

                R.id.radio_not_done -> {
                    status = QuizStatus.NOT_DONE
                    currentFilter = currentFilter.copy(duration = null, score = null)
                }

                else -> {
                    status = QuizStatus.ALL
                    currentFilter = currentFilter.copy(duration = null, score = null)
                }
            }
            currentFilter = currentFilter.copy(status = status)
            viewBinding.layoutBottomFilter.layout3.isVisible = shouldShow
            viewBinding.layoutBottomFilter.layout4.isVisible = shouldShow
            applyFilters()
        }

        viewBinding.layoutBottomFilter.tvFromDate.setOnClickListener {
            showDatePicker { selectedDate ->
                viewBinding.layoutBottomFilter.tvFromDate.text = selectedDate
                currentFilter = currentFilter.copy(fromDate = selectedDate)
                applyFilters()
            }
        }

        viewBinding.layoutBottomFilter.tvToDate.setOnClickListener {
            showDatePicker { selectedDate ->
                viewBinding.layoutBottomFilter.tvToDate.text = selectedDate
                currentFilter = currentFilter.copy(toDate = selectedDate)
                applyFilters()
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
        viewBinding.layoutBottomFilter.icReload.setOnDebounceClick {
            currentFilter = currentFilter.copy(fromDate = null, toDate = null)
            viewBinding.layoutBottomFilter.tvFromDate.text = "Từ ngày"
            viewBinding.layoutBottomFilter.tvToDate.text = "Đến ngày"
            applyFilters()
        }
    }

    private fun updateUi(state: DataResult<SearchQuizState>?) {
        state?.doIfSuccess { data ->
            when (data) {
                is SearchQuizState.ListFilerItem -> {
                    filterData = data.data as MutableList<QuizFilterItem>
                    adapter.submitList(filterData)
                    applyFilters()
                }

                is SearchQuizState.ListCategory -> {
                    val listCategory = data.data.map { it.name }
                    listCategory as MutableList
                    listCategory.add(0, "No")
                    viewBinding.layoutBottomFilter.spinnerContent.setItems(listCategory)
                }

                else -> {}
            }
        }
        state?.doIfFailure { }

    }

    private fun onClickQuiz(item: QuizFilterItem) {
        mainNavigator.offerNavEvent(GoToQuizFragment(item.quiz))
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
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
        val filtered = filterData.filter { item ->
            val matchesKeyword = item.quiz.title.contains(currentFilter.keyword, ignoreCase = true)

            val matchesStatus = when (currentFilter.status) {
                QuizStatus.ALL -> true
                QuizStatus.DONE -> item.isDone
                QuizStatus.NOT_DONE -> !item.isDone
            }

            val matchesCategory = currentFilter.category?.let {
                item.category.name == it || it == "No"
            } ?: true

            val matchesDuration = currentFilter.duration?.let {
                checkDuration(item.quiz.duration, it)
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

            matchesKeyword && matchesStatus && matchesCategory && matchesDuration && matchesScore && matchesDateRange
        }

        adapter.submitList(filtered)
    }

    private fun checkDuration(duration: Int, filter: String): Boolean {
        if (filter == "No")
            return true
        return when (filter) {
            "0-30 phút" -> duration <= 30 * 60
            "30 phút - 60 phút" -> duration in 31 * 60..60 * 60
            "60 phút+" -> duration > 60
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