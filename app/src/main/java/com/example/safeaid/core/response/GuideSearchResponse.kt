package com.example.safeaid.core.response

data class GuideSearchResponse(
    val success: Boolean,
    val message: String,
    val keyword: String,
    val category_id: String?,
    val pagination: PaginationInfo,
    val guides: List<Guide>
)

data class PaginationInfo(
    val total: Int,
    val page: Int,
    val limit: Int
)