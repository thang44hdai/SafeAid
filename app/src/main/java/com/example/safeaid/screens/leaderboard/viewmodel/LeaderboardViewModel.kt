package com.example.safeaid.screens.leaderboard.viewmodel


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.base.BaseViewModel
import com.example.safeaid.core.response.LeaderboardEntry
import com.example.safeaid.core.response.PersonalRankResponse
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.Prefs
import com.example.safeaid.core.utils.doIfFailure
import com.example.safeaid.core.utils.doIfSuccess
import com.example.safeaid.core.utils.onLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LeaderboardState {
    object Loading : LeaderboardState()
    data class Success(val entries: List<LeaderboardEntry>) : LeaderboardState()
    data class Error(val message: String) : LeaderboardState()
}

sealed class LeaderboardEvent {
    object LoadLeaderboard : LeaderboardEvent()
    object LoadPersonalRank : LeaderboardEvent()
}

sealed class PersonalRankState {
    object Loading : PersonalRankState()
    data class Success(val data: PersonalRankResponse) : PersonalRankState()
    data class Error(val message: String) : PersonalRankState()
    object NoData : PersonalRankState()
}

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : BaseViewModel<LeaderboardState, LeaderboardEvent>() {

    private val _leaderboardData = MutableLiveData<LeaderboardState>()
    val leaderboardData: LiveData<LeaderboardState> = _leaderboardData

    private val _personalRankData = MutableLiveData<PersonalRankState>()
    val personalRankData: LiveData<PersonalRankState> = _personalRankData

    fun loadLeaderboard() {
        _leaderboardData.value = LeaderboardState.Loading
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getLeaderboard("Bearer ${getToken()}") },
                callback = { result ->
                    result.onLoading {
                        _leaderboardData.value = LeaderboardState.Loading
                    }
                    result.doIfSuccess { response ->
                        _leaderboardData.value = LeaderboardState.Success(response.data)
                    }
                    result.doIfFailure { error ->
                        _leaderboardData.value = LeaderboardState.Error(error.message ?: "Unknown error")
                    }
                }
            )
        }
    }

    fun loadPersonalRank() {
        _personalRankData.value = PersonalRankState.Loading
        viewModelScope.launch {
            ApiCaller.safeApiCall(
                apiCall = { apiService.getMyRank("Bearer ${getToken()}") },
                callback = { result ->
                    result.onLoading {
                        _personalRankData.value = PersonalRankState.Loading
                    }
                    result.doIfSuccess { response ->
                        if (response.success && response.stats.has_attempted_quizzes) {
                            _personalRankData.value = PersonalRankState.Success(response)
                        } else {
                            _personalRankData.value = PersonalRankState.NoData
                        }
                    }
                    result.doIfFailure { error ->
                        _personalRankData.value = PersonalRankState.Error(error.message ?: "Unknown error")
                    }
                }
            )
        }
    }

    private fun getToken(): String {
        return Prefs.getToken(context) ?: ""
    }

    override fun onTriggerEvent(event: LeaderboardEvent) {
        when (event) {
            LeaderboardEvent.LoadLeaderboard -> loadLeaderboard()
            LeaderboardEvent.LoadPersonalRank -> loadPersonalRank()
        }
    }
}