package com.example.safeaid.screens.bookmark

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.FragmentBookmarkBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.PopBackStack
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.screens.guide.GoToCategoryGuides
import com.example.safeaid.screens.guide.GuideItem

class BookmarkFragment : BaseFragment<FragmentBookmarkBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<String>()
    private lateinit var bookmarkAdapter: BookmarkAdapter

    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        setupRecyclerView()
        loadBookmarks()
    }

    override fun onInitObserver() {}

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
        // Load bookmarked guides from local storage
        // This is just example data
        val bookmarks = listOf(
            GuideItem("1", "Hoả Hoạn", "Quy trình thoát hiểm khi có cháy"),
            GuideItem("2", "Hoả Hoạn", "Quy trình thoát hiểm khi có cháy"),
            GuideItem("3", "Hoả Hoạn", "Quy trình thoát hiểm khi có cháy")
        )
        bookmarkAdapter.submitList(bookmarks)
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
        // Delete selected items from local storage
        // Reload the list
        loadBookmarks()
        exitSelectionMode()
    }

    private fun navigateToGuideDetail(guide: GuideItem) {
        if (!isSelectionMode) {
            val bundle = Bundle().apply {
                putString("guideId", guide.id)
            }
            mainNavigator.offerNavEvent(GoToCategoryGuides(bundle))
        }
    }

    private fun updateDeleteButtonVisibility() {
        viewBinding.btnDeleteSelected.visibility = if (selectedItems.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}