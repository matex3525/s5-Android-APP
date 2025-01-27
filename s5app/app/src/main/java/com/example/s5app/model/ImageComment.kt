package com.example.s5app.model

import com.example.s5app.network.GetGivenPhotoCommentParams

class ImageComment(
    val commentId: String,
    val text: String,
    val time: Long
) {
    companion object {
        fun fromGetGivenPhotoCommentParams(
            params: GetGivenPhotoCommentParams
        ): ImageComment {
            return ImageComment(
                params.commentId,
                params.text,
                params.time
            )
        }
    }
}
