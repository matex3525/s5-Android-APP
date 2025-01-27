package com.example.s5app.use_case

import com.example.s5app.network.ApiResult
import com.example.s5app.network.CupidApiRepository

class DeleteCommentFromPhotoUseCase(
    private val repository: CupidApiRepository
) {
    suspend operator fun invoke(userToken: String, imageId: String, commentId: String, adminToken: String) {
        repository.deleteCommentFromPhoto(userToken, imageId, commentId, adminToken)
    }
}