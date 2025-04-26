package com.example.safeaid.core.network

import com.example.safeaid.core.response.NewsListResponse
import com.example.safeaid.core.service.ApiService
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun fetchAllNews(): Response<NewsListResponse> =
        apiService.getNewsList()
}
