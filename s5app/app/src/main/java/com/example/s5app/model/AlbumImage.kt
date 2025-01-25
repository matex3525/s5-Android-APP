package com.example.s5app.model

import com.example.s5app.network.GetGivenEventPhotoParams

data class AlbumImage(
    val imageId: String,
    val width: Int,
    val height: Int,
    val description: String,
    val pixels: String
) {
    companion object {
        fun fromGetGivenEventPhotoParam(
            params: GetGivenEventPhotoParams
        ) = AlbumImage(
            imageId = params.imageId,
            width = params.width,
            height = params.height,
            description = params.description,
            pixels = params.pixels
        )
    }
}