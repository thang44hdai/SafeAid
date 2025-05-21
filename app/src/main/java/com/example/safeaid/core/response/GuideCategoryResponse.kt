package com.example.safeaid.core.response

import com.google.gson.annotations.SerializedName

data class GuideCategoryResponse(
    @SerializedName("category_id")
    val categoryId: String = "",
    
    @SerializedName("name")
    val name: String = "",
    
    @SerializedName("description")
    val description: String = "",
    
    @SerializedName("guides")
    val guides: List<Guide> = emptyList()
)

data class GuideSummary(
    @SerializedName("guide_id")
    val guideId: String = "",
    
    @SerializedName("title")
    val title: String = ""
)