package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class AddCommentToPhotoRequest(
    @SerializedName("text")
    val text: String
)
