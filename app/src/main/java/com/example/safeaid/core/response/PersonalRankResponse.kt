package com.example.safeaid.core.response

data class PersonalRankResponse(
    val success: Boolean,
    val user: UserProfileInfo,
    val stats: UserStats
)

data class UserProfileInfo(
    val user_id: String,
    val username: String,
    val avatar: String?,
    val email: String,
    val phone_number: String,
    val role: String,
)

data class UserStats(
    val total_score: Int,
    val overall_accuracy: Int,
    val progress: Progress,
    val rank: Int,
    val total_participants: Int,
    val has_attempted_quizzes: Boolean,
    val last_updated: String,
    val quizzes: List<QuizStats>
)

data class Progress(
    val done: Int,
    val total: Int,
    val percentage: Int
)

data class QuizStats(
    val quiz_id: String,
    val title: String,
    val description: String,
    val thumbnail_url: String,
    val category_id: String,
    val best_score: Int,
    val max_score: Int,
    val accuracy: Int
)