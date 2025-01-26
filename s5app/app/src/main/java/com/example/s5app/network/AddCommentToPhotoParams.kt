package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class AddCommentToPhotoParams(
    @SerializedName("comment_id")
    val commentId: String,
    @SerializedName("time")
    val time: Long
)
