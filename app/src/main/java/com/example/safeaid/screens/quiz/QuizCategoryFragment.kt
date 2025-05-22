package com.example.safeaid.screens.quiz

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentQuizCategoryBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.core.response.Category
import com.example.safeaid.core.response.QuizAttempt
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.quiz.data.ItemQuizCategory
import com.example.safeaid.screens.quiz.viewholder.QuizCategoryViewHolder
import com.example.safeaid.screens.quiz.viewholder.QuizHistoryVH
import com.example.safeaid.screens.quiz.viewholder.QuizViewHolder
import com.example.safeaid.screens.quiz.viewmodel.QuizCategoryState
import com.example.safeaid.screens.quiz.viewmodel.QuizCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.ceil

@AndroidEntryPoint
class QuizCategoryFragment : BaseFragment<FragmentQuizCategoryBinding>() {
    private val viewModel: QuizCategoryViewModel by activityViewModels()
    private val mainNavigator: MainNavigator by activityViewModels()
    private var listCategory: MutableList<ItemQuizCategory> = mutableListOf()

    private val categoryAdapter = adapterOf<ItemQuizCategory> {
        diff(
            areItemsTheSame = { old, new -> old.id == new.id || old.quizCategory.categoryId == new.quizCategory.categoryId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.item_quiz_category,
            viewHolder = { view -> QuizCategoryViewHolder(view, ::onClickCategory) }
        )
    }

    private val quizAdapter = adapterOf<Quizze> {
        diff(
            areItemsTheSame = { old, new -> old.quizId == new.quizId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.quiz_item,
            viewHolder = { view -> QuizViewHolder(view, ::onClickQuiz) }
        )
    }

    private val quizHistoryAdapter = adapterOf<QuizAttempt> {
        diff(
            areItemsTheSame = { old, new -> old.attemptId == new.attemptId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.quiz_history_item,
            viewHolder = { view -> QuizHistoryVH(view, ::onClickQuiz) }
        )
    }


    override fun isHostFragment(): Boolean {
        return true
    }

    override fun onInit() {
        val itemHeight = getItemViewHeight(R.layout.quiz_item)
        val totalHeight = itemHeight * viewModel.pageSize
        viewBinding.rcvQuiz.layoutParams = viewBinding.rcvQuiz.layoutParams.apply {
            height = totalHeight
        }

        viewBinding.rcvCategory.adapter = categoryAdapter
        viewBinding.rcvQuiz.adapter = quizAdapter
        viewBinding.rcvDone.adapter = quizHistoryAdapter
        viewModel.getQuizCategory()
        viewModel.getHistoryQuiz()
//        fakeData()
    }

    override fun onInitObserver() {
        viewModel.viewState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> updateUi(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onInitListener() {
        viewBinding.icUp.setOnDebounceClick {
            if (viewModel.currentPage > 0)
                viewModel.currentPage--
            updateQuizPage()
        }

        viewBinding.icDown.setOnDebounceClick {
            if (viewModel.currentPage < viewModel.maxPage)
                viewModel.currentPage++
            updateQuizPage()
        }

        viewBinding.icSearch.setOnDebounceClick {
            mainNavigator.offerNavEvent(GoToSearchFragment(viewBinding.tvSearch.text.toString()))
        }
    }

    private fun updateUi(state: DataResult<QuizCategoryState>?) {
        state?.doIfSuccess { data ->
            when (data) {
                is QuizCategoryState.ListCategory -> {
                    listCategory = data.categories.mapIndexed { index, category ->
                        ItemQuizCategory(
                            quizCategory = category,
                            isSelected = index == viewModel.selectedQuizCategoryPostition
                        )
                    }.toMutableList()
//------------------------------------------------------------
//                    fakeData()
//-----------------------------------------------------------
                    categoryAdapter.submitList(listCategory)

                    val listItemSize =
                        listCategory[viewModel.selectedQuizCategoryPostition].quizCategory.quizzes.size

                    viewModel.maxPage = ceil(listItemSize.toDouble() / viewModel.pageSize).toInt()
                    viewModel.currentPage = 1

                    updateQuizPage()
                }

                is QuizCategoryState.HistoryQuestions -> {
                    quizHistoryAdapter.submitList(data.quizzes.quizAttempts)
                    viewBinding.tvDone.text = "Đã làm ${data.quizzes.quizAttempts?.size ?: 0} bài"
                }

                else -> {}
            }
        }
        state?.doIfFailure { }

    }

    private fun onClickCategory(item: ItemQuizCategory) {
        listCategory = listCategory.map {
            it.copy(isSelected = it.quizCategory.categoryId == item.quizCategory.categoryId)
        } as MutableList<ItemQuizCategory>

        categoryAdapter.submitList(listCategory)
        val index = listCategory.indexOfFirst { it.isSelected }
        viewModel.selectedQuizCategoryPostition = index

        val listItemSize = listCategory[index].quizCategory.quizzes.size
        viewModel.maxPage = ceil(listItemSize.toDouble() / viewModel.pageSize).toInt()
        viewModel.currentPage = 1
        updateQuizPage()
    }

    private fun onClickQuiz(item: Quizze) {
        mainNavigator.offerNavEvent(GoToQuizFragment(item))
    }

    private fun updateQuizPage() {
        val fullListItem =
            listCategory[viewModel.selectedQuizCategoryPostition].quizCategory.quizzes

        val fromIndex = maxOf((viewModel.currentPage - 1) * viewModel.pageSize, 0)
        val toIndex = minOf(fromIndex + viewModel.pageSize, fullListItem.size)

        val listItem = fullListItem.subList(fromIndex, toIndex)

        quizAdapter.submitList(listItem)
        viewBinding.tvPage.text = "${viewModel.currentPage} / ${viewModel.maxPage}"
        if (viewModel.currentPage == 1) {
            viewBinding.icUp.isInvisible = true
        } else if (viewModel.currentPage == viewModel.maxPage) {
            viewBinding.icDown.isInvisible = true
        } else {
            viewBinding.icUp.isInvisible = false
            viewBinding.icDown.isInvisible = false
        }


    }

    private fun getItemViewHeight(@LayoutRes layoutRes: Int): Int {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(layoutRes, null)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            Resources.getSystem().displayMetrics.widthPixels,
            View.MeasureSpec.AT_MOST
        )
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)
        return view.measuredHeight
    }

    private fun fakeData() {
        val fakeCategories = listOf(
            Category(
                categoryId = "1",
                description = "Mô tả Toán",
                name = "Toán học",
                quizzes = List(9) { index ->
                    Quizze(
                        description = "Bài kiểm tra Toán số ${index + 1}",
                        duration = 30 + index * 5,
                        quizId = "math_quiz_${index + 1}",
                        thumbnailUrl = "https://example.com/math_${index + 1}.jpg",
                        title = "Toán cấp độ ${index + 1}",
                        guideId = "guide_${index + 1}",
                    )
                }
            ),
            Category(
                categoryId = "2",
                description = "Mô tả Lý",
                name = "Vật lý",
                quizzes = emptyList()
            )
        )

        listCategory = fakeCategories.mapIndexed { index, category ->
            ItemQuizCategory(
                quizCategory = category,
                isSelected = index == viewModel.selectedQuizCategoryPostition
            )
        }.toMutableList()

        categoryAdapter.submitList(listCategory)
    }
}

class GoToQuizFragment(val item: Quizze) : BaseContainerFragment.NavigationEvent()
class GoToSearchFragment(val search: String) : BaseContainerFragment.NavigationEvent()