package com.example.safeaid.core.network

import com.example.safeaid.core.response.NewsListResponse
import com.example.safeaid.core.service.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun fetchAllNews(): Response<NewsListResponse> =
        apiService.getNewsList()

    suspend fun postNews(
        bearerToken: String,
        title: RequestBody,
        content: RequestBody,
        thumbnailPath: RequestBody,
        media: List<MultipartBody.Part>
    ): Response<NewsListResponse> {
        return apiService.createNews(
            bearerToken = bearerToken,
            title = title,
            content = content,
            thumbnail = thumbnailPath,
            media = media
        )
    }
}
