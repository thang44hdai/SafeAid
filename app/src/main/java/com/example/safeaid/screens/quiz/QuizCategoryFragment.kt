package com.example.safeaid.screens.quiz

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentQuizCategoryBinding
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.screens.quiz.data.ItemQuizCategory
import com.example.safeaid.screens.quiz.viewholder.QuizCategoryViewHolder
import com.example.safeaid.screens.quiz.viewholder.QuizViewHolder
import com.example.safeaid.screens.quiz.viewmodel.QuizCategoryState
import com.example.safeaid.screens.quiz.viewmodel.QuizCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class QuizCategoryFragment : BaseFragment<FragmentQuizCategoryBinding>() {
    private val viewModel: QuizCategoryViewModel by activityViewModels()
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

    override fun isHostFragment(): Boolean {
        return true
    }

    override fun onInit() {
        viewBinding.rcvCategory.adapter = categoryAdapter
        viewBinding.rcvQuiz.adapter = quizAdapter
        viewModel.getQuizCategory()
    }

    override fun onInitObserver() {
        viewModel.viewState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> updateUi(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onInitListener() {
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

                    categoryAdapter.submitList(listCategory)
                    if (listCategory.size > 0)
                        quizAdapter.submitList(listCategory[viewModel.selectedQuizCategoryPostition].quizCategory.quizzes)
                }
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
        quizAdapter.submitList(listCategory[index].quizCategory.quizzes)
    }

    private fun onClickQuiz(item: Quizze) {

    }
}