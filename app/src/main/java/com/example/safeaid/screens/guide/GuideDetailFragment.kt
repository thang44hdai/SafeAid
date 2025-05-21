package com.example.safeaid.screens.guide

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.androidtraining.databinding.FragmentGuideDetailBinding
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.guide.viewmodel.GuideState
import com.example.safeaid.screens.guide.viewmodel.GuideViewModel
import com.example.safeaid.screens.guide.tablayout.GuideDetailPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class GuideDetailFragment : BaseFragment<FragmentGuideDetailBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: GuideViewModel by viewModels()
    private var categoryId: String? = null

    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        categoryId = arguments?.getString("categoryId")
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

        viewModel.guideSteps.observe(viewLifecycleOwner) { steps ->

            // Gắn adapter khi đã có dữ liệu
            val pagerAdapter = GuideDetailPagerAdapter(this, steps, categoryId ?: "")
            viewBinding.viewPager.adapter = pagerAdapter

            // Attach TabLayout lại
            TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Hướng dẫn"
                    1 -> "Liên quan"
                    2 -> "Đánh giá"
                    else -> null
                }
            }.attach()
        }
    }

    override fun onInitListener() {
        // Setup back button click listener
        viewBinding.btnBack.setOnClickListener {
            mainNavigator.offerNavEvent(PopBackStack())
        }

        viewBinding.btnBookmark.setOnClickListener {
            // Lấy guideId từ arguments
            arguments?.getString("guideId")?.let { guideId ->
                viewModel.addToFavourite(guideId)
                // Có thể thêm Toast thông báo thành công
                Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.btnBookmark.setOnLongClickListener {
            mainNavigator.offerNavEvent(GoToBookmark())
            true
        }
    }

    private fun handleSuccess(state: GuideState) {
        when (state) {
            is GuideState.GuideDetail -> {
                state.guide?.let { guide ->
                    viewBinding.tvGuideTitle.text = guide.title
                    viewBinding.tvGuideDescription.text = guide.description
//                    Glide.with(viewBinding.ivGuideImage.context)
//                        .load(guide.media[0].mediaUrl)
//                        .into(viewBinding.ivGuideImage)
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