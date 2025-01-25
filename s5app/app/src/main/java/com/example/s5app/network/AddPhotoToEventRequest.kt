package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class AddPhotoToEventRequest(
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("pixels")
    val pixels: String
)