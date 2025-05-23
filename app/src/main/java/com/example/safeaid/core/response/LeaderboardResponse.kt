package com.example.safeaid.core.response

data class LeaderboardResponse(
    val success: Boolean,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val data: List<LeaderboardEntry>
)

data class LeaderboardEntry(
    val rank: Int,
    val user_id: String,
    val username: String,
    val avatar: String?,
    val total_score: Int,
    val last_updated: String
)