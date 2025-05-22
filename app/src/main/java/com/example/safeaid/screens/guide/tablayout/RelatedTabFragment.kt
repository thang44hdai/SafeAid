package com.example.safeaid.screens.guide.tablayout

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.FragmentRelatedTabBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.core.response.Guide
import com.example.safeaid.core.response.GuideCategoryResponse
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.guide.GoToGuideDetail
import com.example.safeaid.screens.guide.GuideListAdapter
import com.example.safeaid.screens.guide.viewmodel.GuideState
import com.example.safeaid.screens.guide.viewmodel.GuideViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RelatedTabFragment : BaseFragment<FragmentRelatedTabBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: GuideViewModel by viewModels()
    private lateinit var guideAdapter: GuideListAdapter

    companion object {
        fun newInstance(categoryId: String): RelatedTabFragment {
            val fragment = RelatedTabFragment()
            val args = Bundle()
            args.putString("categoryId", categoryId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        setupRecyclerView()
        loadRelatedGuides()
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

    private fun setupRecyclerView() {
        guideAdapter = GuideListAdapter(
            onCategoryClick = {},  // Không cần xử lý click category trong tab này
            onGuideClick = { guide ->
                navigateToGuideDetail(guide)
            }
        )

        viewBinding.rvRelatedGuides.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = guideAdapter
        }
    }

    private fun loadRelatedGuides() {
        val categoryId = arguments?.getString("categoryId")
        categoryId?.let {
            viewModel.loadGuidesByCategory(it)
        }
    }

    private fun handleSuccess(state: GuideState) {
        when (state) {
            is GuideState.ListGuidesByCategory -> {
                guideAdapter.submitList(state.guides)
            }
            else -> {}
        }
    }

    private fun handleError() {}

    private fun handleLoading() {}

    private fun navigateToGuideDetail(guide: Guide) {
        val bundle = Bundle().apply {
            putString("guideId", guide.guideId)
            putString("guideTitle", guide.title)
            putString("categoryId", arguments?.getString("categoryId"))  // Thêm dòng này
        }
        mainNavigator.offerNavEvent(GoToGuideDetail(bundle))
    }
}