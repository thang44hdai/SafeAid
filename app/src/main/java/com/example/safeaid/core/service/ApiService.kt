package com.example.safeaid.core.service

import QuizCategoryResponse
import com.example.safeaid.core.request.LoginRequest
import com.example.safeaid.core.request.QuizAttemptRequest
import com.example.safeaid.core.response.CommentDto
import com.example.safeaid.core.response.CommentListResponse
import com.example.safeaid.core.response.CreatePostResponse
import com.example.safeaid.core.response.LoginResponse
import com.example.safeaid.core.response.PostListResponse
import com.example.safeaid.core.response.QuizAttemptResponse
import com.example.safeaid.core.response.QuizHistoryDetailResponse
import com.example.safeaid.core.response.QuizResponse
import kotlinx.serialization.json.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // quiz
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

    //community
    @GET("/api/posts")
    suspend fun getPosts(
        @Header("Authorization") bearerToken: String,
        @Query("page")  page: Int   = 1,
        @Query("limit") limit: Int  = 10
    ): Response<PostListResponse>

    @Multipart
    @POST("/api/posts")
    suspend fun createPost(
        @Header("Authorization") bearerToken: String,
        @Part("content") content: RequestBody,
        @Part("title") title: RequestBody?,
        @Part images: List<MultipartBody.Part>?
    ): Response<CreatePostResponse>

    @GET("/api/posts/{postId}/comments")
    suspend fun getComments(
        @Header("Authorization") bearer: String,
        @Path("postId") postId: String
    ): Response<List<CommentDto>>

    @POST("/api/posts/{postId}/comments")
    suspend fun createComment(
        @Header("Authorization") bearer: String,
        @Path("postId") postId: String,
        @Body body: Map<String, String>
    ): Response<CommentDto>

    @POST("/api/posts/{postId}/like")
    suspend fun likePost(
        @Header("Authorization") bearer: String,
        @Path("postId") postId: String
    ): Response<Unit>

    @DELETE("/api/posts/{postId}/like")
    suspend fun unLikePost(
        @Header("Authorization") bearer: String,
        @Path("postId") postId: String
    ): Response<Unit>

    //Authentication
    @POST("/api/auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): retrofit2.Response<LoginResponse>
}