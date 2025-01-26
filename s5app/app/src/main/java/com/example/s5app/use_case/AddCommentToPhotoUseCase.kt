package com.example.s5app.use_case

import com.example.s5app.network.AddCommentToPhotoParams
import com.example.s5app.network.AddCommentToPhotoRequest
import com.example.s5app.network.ApiResult
import com.example.s5app.network.CupidApiRepository

class AddCommentToPhotoUseCase(
    private val repository: CupidApiRepository
) {
    suspend operator fun invoke(userToken: String, imageId: String, request: AddCommentToPhotoRequest): ApiResult<AddCommentToPhotoParams> {
        return repository.addCommentToPhoto(userToken, imageId, request)
    }
}