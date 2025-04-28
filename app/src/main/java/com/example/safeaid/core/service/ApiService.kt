package com.example.safeaid.core.service

import QuizCategoryResponse
import com.example.safeaid.core.request.QuizAttemptRequest
import com.example.safeaid.core.response.QuizAttemptResponse
import com.example.safeaid.core.response.QuizResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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
}