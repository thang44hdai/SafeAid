package com.example.safeaid.screens.guide

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.androidtraining.databinding.FragmentStepDetailBinding
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.response.GuideStepResponse
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.ErrorResponse
import com.example.safeaid.screens.guide.viewmodel.GuideState
import com.example.safeaid.screens.guide.viewmodel.GuideViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.getValue

@AndroidEntryPoint
class StepDetailFragment : BaseFragment<FragmentStepDetailBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: GuideViewModel by viewModels()

    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        val step = arguments?.getParcelable<GuideStepResponse>("step")
        loadGuideCategories()
        setupUI(step)
    }

    override fun onInitObserver() {
        viewModel.viewState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is DataResult.Success -> handleSuccess(state.data)
                    is DataResult.Error -> handleError(state.error)
                    is DataResult.Loading -> handleLoading()
                    null -> {}
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onInitListener() {
        viewBinding.btnBack.setOnClickListener {
            mainNavigator.offerNavEvent(PopBackStack())
        }

        viewBinding.btnPlay.setOnClickListener {
            // Handle video playback
        }
    }

    private fun setupUI(step: GuideStepResponse?) {
        step?.let {
            viewBinding.apply {
                tvStepTitle.text = it.title
                tvDescription.text = it.content
            }
        }
    }

    private fun loadGuideCategories() {
        val step = arguments?.getParcelable<GuideStepResponse>("step")
        viewModel.loadGuideStepMedia(step?.stepId ?: "")
    }

    private fun handleSuccess(state: GuideState) {
        when (state) {
            is GuideState.GuideStepMediaDetail -> {
                val thumbnail = state.media?.firstOrNull()?.mediaURL
                if (!thumbnail.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(thumbnail)
                        .into(viewBinding.ivThumbnail)

                    Glide.with(requireContext())
                        .load(thumbnail) // hoặc dùng icon nút play tùy bạn
                        .into(viewBinding.btnPlay)
                }
            }
            else -> {}
        }
    }


    private fun handleError(state: ErrorResponse) {
        when (state) {
            else -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLoading() {}
}