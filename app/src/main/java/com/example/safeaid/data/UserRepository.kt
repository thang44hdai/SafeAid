package com.example.safeaid.data

import android.util.Log
import com.example.safeaid.core.response.DecisionUserRes
import com.example.safeaid.core.service.ApiService
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(
    val apiService: ApiService
) {
    suspend fun getDecisionUser(
        version: Int,
        dataset: String,
    ): Flow<DataResult<DecisionUserRes>> =
        callbackFlow {
            withContext(Dispatchers.IO) {
                apiService.getDecisionUser(
                    "PHPSESSID=28gte4gi99tljiv1bh0bun6mk5",
                    version,
                    dataset
                ).enqueue(object : Callback<DecisionUserRes> {
                    override fun onResponse(
                        call: Call<DecisionUserRes>,
                        response: Response<DecisionUserRes>
                    ) {
                        if (response.isSuccessful) {
                            trySend(DataResult.Success(response.body()!!))
                        } else {
                            trySend(
                                DataResult.Error(
                                    ErrorResponse(
                                        errorType = response.code().toString(),
                                        message = response.errorBody()?.charStream()?.readText()
                                            ?: response.message(),
                                        errorCode = response.code()
                                    )
                                )
                            )
                        }
                    }

                    override fun onFailure(call: Call<DecisionUserRes>, t: Throwable) {
                        trySend(
                            DataResult.Error(ErrorResponse("1", t.message.toString()))
                        )
                    }
                })
            }

            awaitClose { }
        }

}
