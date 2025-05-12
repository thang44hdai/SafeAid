package com.example.safeaid.screens.guide

data class GuideItem(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailPath: String? = null,
    val categoryName: String = ""
)