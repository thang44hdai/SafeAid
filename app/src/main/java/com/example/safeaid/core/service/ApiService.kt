package com.example.safeaid.core.service

import com.example.safeaid.common.Const.BASE_URL
import com.example.safeaid.core.response.DecisionUserRes
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
//    @POST("/nomurakabu/api/communication/MobileLogin")
//    fun login()

    @GET("/nomurakabu/api/communication/GetHomeData")
    fun getDecisionUser(
        @Header("Cookie") cookie: String,
        @Query("version") version: Int,
        @Query("dataset") dataset: String,
    ): Call<DecisionUserRes>
}