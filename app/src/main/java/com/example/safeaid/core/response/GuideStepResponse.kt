package com.example.safeaid.core.response

import GuideStep
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GuideStepResponse(
    @SerializedName("step_id") val stepId: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("order_index") val orderIndex: Int,
    @SerializedName("guide_id") val guideId: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        stepId = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        content = parcel.readString() ?: "",
        orderIndex = parcel.readInt(),
        guideId = parcel.readString() ?: "",
        createdAt = parcel.readString() ?: "",
        updatedAt = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stepId)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeInt(orderIndex)
        parcel.writeString(guideId)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GuideStepResponse> {
        override fun createFromParcel(parcel: Parcel): GuideStepResponse = GuideStepResponse(parcel)
        override fun newArray(size: Int): Array<GuideStepResponse?> = arrayOfNulls(size)
    }
}

// Extension function để chuyển đổi từ Response sang Model
fun GuideStepResponse.toGuideStep() = GuideStep(
    step_id = stepId,
    title = title,
    content = content,
    order_index = orderIndex,
    guide_id = guideId,
    created_at = createdAt,
    updated_at = updatedAt
)