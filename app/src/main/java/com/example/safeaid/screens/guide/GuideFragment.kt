package com.example.safeaid.screens.guide

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.FragmentGuideBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.core.response.Guide
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.guide.viewmodel.GuideState
import com.example.safeaid.screens.guide.viewmodel.GuideViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.safeaid.core.utils.ErrorResponse
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class GuideFragment : BaseFragment<FragmentGuideBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: GuideViewModel by viewModels()
    
    private lateinit var guideListAdapter: GuideListAdapter
    
    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        setupRecyclerView()
        setupSearchView()
        loadGuideCategories()
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
        // No additional listeners needed
    }
    
    private fun setupRecyclerView() {
        guideListAdapter = GuideListAdapter(
            onCategoryClick = { category ->
                val bundle = Bundle().apply {
                    putString("categoryId", category.categoryId)
                    putString("categoryName", category.name)
                }
                mainNavigator.offerNavEvent(GoToCategoryGuides(bundle))
            },
            onGuideClick = { guide ->
                navigateToGuideDetail(guide)
            }
        )

        viewBinding.rvEmergency.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = guideListAdapter
        }
    }

    private fun navigateToGuideDetail(guide: Guide) {
        val bundle = Bundle().apply {
            putString("guideId", guide.guideId)
            putString("guideTitle", guide.title)
        }
        mainNavigator.offerNavEvent(GoToGuideDetail(bundle))
    }
    
    private fun setupSearchView() {
        viewBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterCategories(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCategories(newText)
                return true
            }
        })
    }
    
    private fun loadGuideCategories() {
        viewModel.getGuideCategories()
    }
    
    private fun filterCategories(query: String?) {
        val filteredCategories = viewModel.filterCategories(query)
        guideListAdapter.submitList(filteredCategories)
    }
    
    private fun handleSuccess(state: GuideState) {
        when (state) {
            is GuideState.ListCategories -> {
                guideListAdapter.submitList(state.categories)
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

// Add navigation event class
class GoToCategoryGuides(override val extras: Bundle? = null) : BaseContainerFragment.NavigationEvent()
class GoToGuideDetail(override val extras: Bundle? = null) : BaseContainerFragment.NavigationEvent()