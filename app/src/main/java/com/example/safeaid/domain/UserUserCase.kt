package com.example.safeaid.domain

import com.example.safeaid.core.response.DecisionUserRes
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.data.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserUserCase @Inject constructor(
    val repository: UserRepository
) {
    suspend fun getDecisionUser(version: Int, dataset: String) : Flow<DataResult<DecisionUserRes>> {
        return repository.getDecisionUser(version, dataset)
    }

}