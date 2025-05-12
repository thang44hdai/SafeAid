package com.example.safeaid.screens.guide

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.androidtraining.databinding.FragmentGuideDetailBinding
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.guide.viewmodel.GuideState
import com.example.safeaid.screens.guide.viewmodel.GuideViewModel
import com.example.safeaid.core.response.GuideStepResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class GuideDetailFragment : BaseFragment<FragmentGuideDetailBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: GuideViewModel by viewModels()
    private val stepAdapter = StepAdapter { step ->
        val bundle = Bundle().apply {
            putParcelable("step", step)
        }
        mainNavigator.offerNavEvent(GoToStepDetail(bundle))
    }

    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        setupRecyclerView()
        loadGuideDetail(arguments?.getString("guideId").toString())  // Gọi API load guide detail
    }

    private fun loadGuideDetail(guideId: String) {
        viewModel.loadGuideDetails(guideId)  // Gọi hàm loadGuideDetails từ ViewModel
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

        viewModel.guideSteps.observe(viewLifecycleOwner) { steps: List<GuideStepResponse> ->
            stepAdapter.submitList(steps)
        }
    }

    override fun onInitListener() {
        // Setup back button click listener
        viewBinding.btnBack.setOnClickListener {
            mainNavigator.offerNavEvent(PopBackStack())
        }
    
        viewBinding.btnBookmark.setOnClickListener {
            mainNavigator.offerNavEvent(GoToBookmark())
        }
    }

    private fun setupRecyclerView() {
        viewBinding.rvSteps.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stepAdapter
        }
    }

    private fun handleSuccess(state: GuideState) {
        when (state) {
            is GuideState.GuideDetail -> {
                state.guide?.let { guide ->
                    viewBinding.tvGuideTitle.text = guide.title
                    viewBinding.tvGuideDescription.text = guide.description
                    Glide.with(viewBinding.ivGuideImage.context)
                        .load(guide.media[0].mediaUrl)
                        .into(viewBinding.ivGuideImage)
                }
            }
            else -> {}
        }
    }

    private fun handleError() {}

    private fun handleLoading() {}
}

// Add navigation event class
class GoToStepDetail(override val extras: Bundle? = null) : BaseContainerFragment.NavigationEvent()

// Add navigation event class
class GoToBookmark : BaseContainerFragment.NavigationEvent()