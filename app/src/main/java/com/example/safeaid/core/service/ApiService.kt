package com.example.safeaid.core.service

import QuizCategoryResponse
import com.example.safeaid.core.request.QuizAttemptRequest
import com.example.safeaid.core.response.Guide
import com.example.safeaid.core.response.GuideCategoryResponse
import com.example.safeaid.core.response.GuideResponse
import com.example.safeaid.core.response.GuideStepMediaResponse
import com.example.safeaid.core.response.GuideStepResponse
import com.example.safeaid.core.response.QuizAttemptResponse
import com.example.safeaid.core.response.QuizHistoryDetailResponse
import com.example.safeaid.core.response.QuizResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("/api/quiz-categories/with-quizzes")
    suspend fun getCategoryQuiz(): Response<QuizCategoryResponse>

    @GET("api/questions/quiz/{quizId}")
    suspend fun getQuestionByQuizId(
        @Path("quizId") quizId: String
    ): Response<QuizResponse>

    @POST("/api/quiz-attempts")
    suspend fun saveResultQuiz(
        @Body request: QuizAttemptRequest
    ): Response<JsonObject>

    @GET("/api/quiz-attempts/user/{userId}")
    suspend fun getResultQuiz(
        @Path("userId") userId: String
    ): Response<QuizAttemptResponse>

    @GET("/api/quiz-attempts/{userId}/{quizId}")
    suspend fun getHistoryOfQuiz(
        @Path("userId") userId: String,
        @Path("quizId") quizId: String
    ): Response<QuizAttemptResponse>

    @GET("/api/quiz-attempts/{quiz_attempt_id}/{quiz_id}/details")
    suspend fun getHistoryDetail(
        @Path("quiz_attempt_id") quizAttemptId: String,
        @Path("quiz_id") quizId: String
    ): Response<QuizHistoryDetailResponse>
    
    // Thêm endpoint mới cho Guide Categories
    @GET("/api/guide-categories")
    suspend fun getGuideCategories(
        @Header("Authorization") token: String
    ): Response<List<GuideCategoryResponse>>

    // Thêm endpoint mới cho Guides
    @GET("/api/guides/")
    suspend fun getGuides(
        @Header("Authorization") token: String
    ): Response<GuideResponse>

    // Thêm endpoint mới cho Guide Detail
    @GET("/api/guides/{guideId}")
    suspend fun getGuideById(
        @Path("guideId") guideId: String,
        @Header("Authorization") token: String
    ): Response<Guide>

    // Sửa lại các endpoint guide step
    @GET("/api/guide-steps/guide/{guideId}")
    suspend fun getGuideSteps(
        @Path("guideId") guideId: String,
        @Header("Authorization") token: String
    ): Response<List<GuideStepResponse>>

    @GET("/api/guide-steps/{stepId}")
    suspend fun getGuideStep(
        @Path("stepId") stepId: String,
        @Header("Authorization") token: String
    ): Response<GuideStepResponse>

    @GET("/api/guide-step-media/step/{stepId}")
    suspend fun getGuideStepMedia(
        @Path("stepId") stepId: String,
        @Header("Authorization") token: String
    ): Response<List<GuideStepMediaResponse>>
}