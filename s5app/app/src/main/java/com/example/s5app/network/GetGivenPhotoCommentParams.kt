package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class GetGivenPhotoCommentParams(
    @SerializedName("comment_id")
    val commentId: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("time")
    val time: Long
)

