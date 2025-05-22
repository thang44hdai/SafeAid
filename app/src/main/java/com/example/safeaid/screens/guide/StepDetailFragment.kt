package com.example.safeaid.screens.guide

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.androidtraining.R
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
                val thumbnail = state.media
                    ?.firstOrNull { it.mediaType == "image/png" }
                    ?.mediaURL

                val videoUrl = state.media
                    ?.firstOrNull { it.mediaType == "video/mp4" }
                    ?.mediaURL
                    ?.replace("localhost", "192.168.1.15")

                if (!thumbnail.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(thumbnail)
                        .into(viewBinding.ivThumbnail)

                    viewBinding.ivThumbnail.setOnClickListener {
                        showFullScreenImage()
                    }
                }

                if (!videoUrl.isNullOrEmpty()) {
                    viewBinding.btnPlay.visibility = View.VISIBLE // đảm bảo hiển thị nút
                    viewBinding.videoContainer.setOnClickListener {
                        showFullScreenVideo(videoUrl)
                    }
                } else {
                    viewBinding.btnPlay.visibility = View.GONE // ẩn nếu không có video
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

    private fun showFullScreenImage() {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        
        // Tạo layout container để chứa PhotoView và nút back
        val container = FrameLayout(requireContext())
        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Thêm PhotoView
        val photoView = com.github.chrisbanes.photoview.PhotoView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            minimumScale = 0.5f
            maximumScale = 3.0f
            mediumScale = 1.5f
            isZoomable = true
            setImageDrawable(viewBinding.ivThumbnail.drawable)
        }

        // Thêm nút back
        val backButton = ImageButton(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                setMargins(32, 32, 0, 0)
            }
            setImageResource(R.drawable.ic_back)
            setBackgroundResource(android.R.color.white) // Đổi background thành màu trắng
            setPadding(24, 30, 24, 30)
            alpha = 0.8f
            elevation = 8f
            outlineProvider = ViewOutlineProvider.BACKGROUND // Thêm để hỗ trợ bo góc
            clipToOutline = true // Cắt theo đường viền
            background = GradientDrawable().apply { // Tạo background hình tròn
                shape = GradientDrawable.OVAL
                setColor(Color.WHITE)
            }
        }

        // Thêm sự kiện click cho nút back
        backButton.setOnClickListener {
            dialog.dismiss()
        }

        // Thêm các view vào container
        container.addView(photoView)
        container.addView(backButton)

        dialog.setContentView(container)
        dialog.show()
    }

    private fun showFullScreenVideo(videoUrl: String) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        // Tạo container cho VideoView và nút back
        val container = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Tạo VideoView
        val videoView = android.widget.VideoView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setVideoPath(videoUrl)
            setOnPreparedListener {
                it.isLooping = true
                start()
            }
            setOnCompletionListener {
                // Tự động lặp lại video nếu muốn
                start()
            }
        }

        // Tạo nút back
        val backButton = ImageButton(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                setMargins(32, 32, 0, 0)
            }
            setImageResource(R.drawable.ic_back)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.WHITE)
            }
            setPadding(24, 30, 24, 30)
            alpha = 0.8f
            elevation = 8f
            clipToOutline = true
        }

        backButton.setOnClickListener {
            videoView.stopPlayback()
            dialog.dismiss()
        }

        // Thêm VideoView và nút back vào container
        container.addView(videoView)
        container.addView(backButton)

        dialog.setContentView(container)
        dialog.show()
    }

}