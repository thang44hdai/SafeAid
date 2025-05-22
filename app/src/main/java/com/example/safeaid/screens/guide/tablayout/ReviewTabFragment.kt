package com.example.safeaid.screens.guide.tablayout

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentReviewTabBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.guide.viewmodel.GuideState
import com.example.safeaid.screens.guide.viewmodel.GuideViewModel
import com.example.safeaid.core.response.Quizze
import com.example.safeaid.core.ui.recyclerview.adapterOf
import com.example.safeaid.screens.quiz.GoToQuizFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ReviewTabFragment : BaseFragment<FragmentReviewTabBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: GuideViewModel by viewModels()

    private val quizAdapter = adapterOf<Quizze> {
        diff(
            areItemsTheSame = { old, new -> old.quizId == new.quizId },
            areContentsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.item_related_quiz,
            viewHolder = { view -> RelatedQuizViewHolder(view, ::onQuizClick) }
        )
    }

    companion object {
        fun newInstance(guideId: String): ReviewTabFragment {
            val fragment = ReviewTabFragment()
            val args = Bundle()
            args.putString("guideId", guideId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        viewBinding.rvQuizzes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = quizAdapter
        }
        val guideId = arguments?.getString("guideId")
        loadRelatedQuizzes(guideId)
    }

    override fun onInitObserver() {
        viewModel.viewState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is DataResult.Success -> handleSuccess(state.data)
                    is DataResult.Error -> handleError()
                    is DataResult.Loading -> handleLoading()
                    null -> {}
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onInitListener() {}

    private fun loadRelatedQuizzes(guideId: String?) {
        guideId?.let {
            viewModel.loadRelatedQuizzes(it)
        }
    }

    private fun handleSuccess(state: GuideState) {
        when (state) {
            is GuideState.RelatedQuizzes -> {
                quizAdapter.submitList(state.quizzes)
            }
            else -> {}
        }
    }

    private fun onQuizClick(quiz: Quizze) {
        mainNavigator.offerNavEvent(GoToQuizFragment(quiz))
    }

    private fun handleError() {}

    private fun handleLoading() {}
}