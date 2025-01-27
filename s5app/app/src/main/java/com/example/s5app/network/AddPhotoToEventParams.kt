package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class AddPhotoToEventParams(
    @SerializedName("image_id")
    val imageId: String
)


