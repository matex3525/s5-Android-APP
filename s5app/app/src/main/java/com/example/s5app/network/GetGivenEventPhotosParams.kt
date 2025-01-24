package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class GetGivenEventPhotosParams(
    @SerializedName("params")
    val params: List<GetGivenEventPhotoParams>
)

data class GetGivenEventPhotoParams(
    @SerializedName("image_id")
    val imageId: String,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("description")
    val description: String,

    @SerializedName("pixels")
    val pixels: String
)
