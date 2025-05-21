package com.example.safeaid.screens.bookmark

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.FragmentBookmarkBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.ErrorResponse
import com.example.safeaid.screens.bookmark.viewholder.BookmarkState
import com.example.safeaid.screens.bookmark.viewholder.BookmarkViewModel
import com.example.safeaid.screens.guide.GoToGuideDetail
import com.example.safeaid.screens.guide.GuideItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class BookmarkFragment : BaseFragment<FragmentBookmarkBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: BookmarkViewModel by viewModels()
    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<String>()
    private lateinit var bookmarkAdapter: BookmarkAdapter

    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        setupRecyclerView()
        // Gọi API để lấy dữ liệu bookmark
        loadBookmarks()
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

        viewBinding.btnSelectAll.setOnClickListener {
            if (isSelectionMode) {
                // Toggle selection mode off
                exitSelectionMode()
            } else {
                // Enter selection mode
                enterSelectionMode()
            }
        }

        viewBinding.btnDeleteSelected.setOnClickListener {
            deleteSelectedItems()
        }
    }

    private fun setupRecyclerView() {
        bookmarkAdapter = BookmarkAdapter(
            onItemClick = { guide -> navigateToGuideDetail(guide) },
            onItemLongClick = { enterSelectionMode() },
            onItemSelected = { id, isSelected ->
                if (isSelected) {
                    selectedItems.add(id)
                } else {
                    selectedItems.remove(id)
                }
                updateDeleteButtonVisibility()
            }
        )

        viewBinding.rvBookmarks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookmarkAdapter
        }
    }

    private fun loadBookmarks() {
        viewModel.loadBookmarks()
    }

    private fun navigateToGuideDetail(guide: GuideItem) {
        if (!isSelectionMode) {
            val bundle = Bundle().apply {
                putString("guideId", guide.id)
                putString("guideTitle", guide.title)
            }
            mainNavigator.offerNavEvent(GoToGuideDetail(bundle))
        }
    }

    private fun enterSelectionMode() {
        isSelectionMode = true
        viewBinding.btnSelectAll.text = "Huỷ"
        viewBinding.btnDelete.visibility = View.VISIBLE
        viewBinding.btnDeleteSelected.visibility = View.VISIBLE
        bookmarkAdapter.setSelectionMode(true)
    }

    private fun exitSelectionMode() {
        isSelectionMode = false
        selectedItems.clear()
        viewBinding.btnSelectAll.text = "Chọn tất cả"
        viewBinding.btnDelete.visibility = View.GONE
        viewBinding.btnDeleteSelected.visibility = View.GONE
        bookmarkAdapter.setSelectionMode(false)
    }

    private fun deleteSelectedItems() {
        selectedItems.forEach { guideId ->
            viewModel.deleteBookmark(guideId)
        }
        exitSelectionMode()
    }

    private fun handleSuccess(state: BookmarkState) {
        when (state) {
            is BookmarkState.ListBookmarks -> {
                bookmarkAdapter.submitList(state.bookmarks)
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

    private fun updateDeleteButtonVisibility() {
        viewBinding.btnDeleteSelected.visibility = if (selectedItems.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}