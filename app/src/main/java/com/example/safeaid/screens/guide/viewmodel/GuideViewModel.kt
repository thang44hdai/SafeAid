package com.example.safeaid.screens.guide.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.Guide
import com.example.safeaid.core.response.GuideCategoryResponse
import com.example.safeaid.core.response.GuideStepMediaResponse
import com.example.safeaid.core.response.GuideStepResponse
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import com.example.safeaid.screens.guide.GuideItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuideViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel<GuideState, GuideEvent>() {
    
    // Token lấy từ SharedPreferences hoặc DataStore trong thực tế
    private val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZTJhNDAzNjQtZTBmNS00YjBmLWIyNDAtZWY0ODM0NTBlMmFkIiwiZW1haWwiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZSI6InVzZXIiLCJpYXQiOjE3NDYwMTgxMTR9.1R9cmZKSC6hED9SWaNdJR0_TC82nk5_QYopGeleyFMI"
    
    // Danh sách categories để lưu trữ và lọc
    private var allCategories: List<GuideCategoryResponse> = emptyList()
    
    // Danh sách guides để lưu trữ và lọc
    private var allGuides: List<GuideItem> = emptyList()
    
     fun getGuideCategories() {
         viewModelScope.launch {
             ApiCaller.safeApiCall(
                 apiCall = { apiService.getGuideCategories(token) },
                 callback = { result ->
                     result.doIfSuccess { categories ->
                         allCategories = categories
                         updateState(DataResult.Success(GuideState.ListCategories(allCategories)))
                     }
                     result.doIfFailure {}
                     result.onLoading {}
                 }
             )
         }
     }
    
    fun getGuides() {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getGuides(token) },
                callback = { result ->
                    result.doIfSuccess { response ->
                        val guideItems = response.guides.map { guide ->
                            GuideItem(
                                id = guide.guideId,
                                title = guide.title,
                                description = guide.description,
                                thumbnailPath = guide.thumbnailPath,
                                categoryName = guide.category.name
                            )
                        }
                        allGuides = guideItems
                        updateState(DataResult.Success(GuideState.ListGuides(guideItems)))
                    }
                    result.doIfFailure {}
                    result.onLoading {}
                }
            )
        }
    }
    
    // Hàm lọc danh sách categories theo từ khóa tìm kiếm
    fun filterCategories(query: String?): List<GuideCategoryResponse> {
        if (query.isNullOrBlank()) return allCategories
        
        return allCategories.filter { category ->
            category.name.contains(query, ignoreCase = true) ||
            category.description.contains(query, ignoreCase = true) ||
            category.guides.any { guide -> guide.title.contains(query, ignoreCase = true) }
        }
    }
    
    // Hàm lọc danh sách guides theo từ khóa tìm kiếm
    fun filterGuides(query: String?): List<GuideItem> {
        if (query.isNullOrBlank()) return allGuides
        
        return allGuides.filter { guide ->
            guide.title.contains(query, ignoreCase = true) ||
            guide.description.contains(query, ignoreCase = true) ||
            guide.categoryName.contains(query, ignoreCase = true)
        }
    }

    private val _selectedGuide = MutableLiveData<Guide>()
    val selectedGuide: LiveData<Guide> = _selectedGuide

    private val _guideSteps = MutableLiveData<List<GuideStepResponse>>()
    val guideSteps: LiveData<List<GuideStepResponse>> = _guideSteps

    private val _stepMedias = MutableLiveData<List<GuideStepMediaResponse>>()
    val stepMedias: LiveData<List<GuideStepMediaResponse>> = _stepMedias

    fun loadGuideDetails(guideId: String) {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getGuideById(guideId, token) },
                callback = { result ->
                    result.doIfSuccess { response ->
                        _selectedGuide.value = response
                        loadGuideSteps(guideId)
                    }
                    result.doIfFailure {}
                    result.onLoading {}
                }
            )
        }
    }

    private fun loadGuideSteps(guideId: String) {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getGuideSteps(guideId, token) },
                callback = { result ->
                    result.doIfSuccess { steps ->
                        _guideSteps.value = steps
                        updateState(DataResult.Success(GuideState.GuideDetail(_selectedGuide.value)))
                    }
                    result.doIfFailure {}
                    result.onLoading {}
                }
            )
        }
    }

    fun loadGuideStepMedia(stepId: String) {
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getGuideStepMedia(stepId, token) },
                callback = { result ->
                    result.doIfSuccess { medias ->
                        _stepMedias.value = medias
                        updateState(DataResult.Success(GuideState.GuideStepMediaDetail(_stepMedias.value)))
                    }
                    result.doIfFailure {}
                    result.onLoading {}
                }
            )
        }
    }

    override fun onTriggerEvent(event: GuideEvent) {
        TODO("Not yet implemented")
    }
}

// State class cho Guide
sealed class GuideState {
    data class ListCategories(val categories: List<GuideCategoryResponse>) : GuideState()
    data class ListGuides(val guides: List<GuideItem>) : GuideState()
    data class Error(val message: String) : GuideState()
    object Loading : GuideState()
    data class GuideDetail(val guide: Guide?) : GuideState()
    data class GuideStepDetail(val step: GuideStepResponse?) : GuideState()
    data class GuideStepMediaDetail(val media: List<GuideStepMediaResponse>?) : GuideState()
}

// Event class cho Guide
sealed class GuideEvent {
    object GetGuideCategories : GuideEvent()
    object GetGuides : GuideEvent()
    data class FilterCategories(val query: String?) : GuideEvent()
    data class FilterGuides(val query: String?) : GuideEvent()
}