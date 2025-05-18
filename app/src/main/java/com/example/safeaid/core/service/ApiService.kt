package com.example.safeaid.core.service

import QuizCategoryResponse
import com.example.safeaid.core.response.NewsListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/api/quiz-categories/with-quizzes")
    suspend fun getCategoryQuiz(): Response<QuizCategoryResponse>

    @GET("/api/news")
    suspend fun getNewsList(): Response<NewsListResponse>

    @Multipart
    @POST("/api/news")
    suspend fun createNews(
        @Header("Authorization") bearerToken: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part thumbnail: MultipartBody.Part?,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<NewsListResponse>
}
