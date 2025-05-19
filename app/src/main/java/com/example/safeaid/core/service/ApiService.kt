package com.example.safeaid.core.service

import QuizCategoryResponse
import com.example.safeaid.core.request.LoginRequest
import com.example.safeaid.core.request.QuizAttemptRequest

import com.example.safeaid.core.response.BookmarkResponse
import com.example.safeaid.core.response.Guide
import com.example.safeaid.core.response.GuideCategoryResponse
import com.example.safeaid.core.response.GuideResponse
import com.example.safeaid.core.response.GuideStepMediaResponse
import com.example.safeaid.core.response.GuideStepResponse

import com.example.safeaid.core.request.RegisterRequest
import com.example.safeaid.core.response.CommentDto
import com.example.safeaid.core.response.CreatePostResponse
import com.example.safeaid.core.response.LoginResponse
import com.example.safeaid.core.response.PostListResponse

import com.example.safeaid.core.response.QuizAttemptResponse
import com.example.safeaid.core.response.QuizHistoryDetailResponse
import com.example.safeaid.core.response.QuizResponse
import com.example.safeaid.core.response.RegisterResponse
import kotlinx.serialization.json.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.example.safeaid.core.response.NewsListResponse
import com.example.safeaid.core.response.NotificationRes
import com.example.safeaid.core.response.QuizIdRes
import com.example.safeaid.core.response.Quizze
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header

import retrofit2.http.Multipart
import retrofit2.http.PATCH

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
        @Header("Authorization") bearerToken: String,
        @Body request: QuizAttemptRequest
    ): Response<JsonObject>

    @GET("/api/quiz-attempts/user")
    suspend fun getResultQuiz(
        @Header("Authorization") bearerToken: String,
    ): Response<QuizAttemptResponse>

    @GET("/api/quiz-attempts/{quizId}")
    suspend fun getHistoryOfQuiz(
        @Header("Authorization") bearerToken: String,
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

    @GET("/api/favourite-guide-lists")
    suspend fun getFavouriteList(
        @Header("Authorization") token: String
    ): Response<BookmarkResponse>

    @DELETE("/api/favourite-guide-items/{guideId}")
    suspend fun deleteFavouriteGuide(
        @Path("guideId") guideId: String,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/api/favourite-guide-items")
    suspend fun addFavouriteGuide(
        @Body request: Map<String, String>, // Thay đổi kiểu dữ liệu của request body
        @Header("Authorization") token: String
    ): Response<Unit>


    //community
    @GET("/api/posts")
    suspend fun getPosts(
        @Header("Authorization") bearerToken: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
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

    @POST("/api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET("/api/news")
    suspend fun getNewsList(): Response<NewsListResponse>

    @Multipart
    @POST("/api/news")
    suspend fun createNews(
        @Header("Authorization") bearerToken: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part thumbnail: RequestBody,
        @Part media: List<MultipartBody.Part>? = null
    ): Response<NewsListResponse>

    @GET("/api/notifications")
    suspend fun getNotifications(
        @Header("Authorization") bearerToken: String,
    ): Response<NotificationRes>

    @GET("/api/quizzes/{quizId}")
    suspend fun getQuizById(
        @Path("quizId") quizId: String,
    ): Response<QuizIdRes>

    @PATCH("/api/notifications/{notification_id}/read")
    suspend fun markAsRead(
        @Header("Authorization") bearerToken: String,
        @Path("notification_id") notificationId: String,
    ): Response<QuizIdRes>
}
