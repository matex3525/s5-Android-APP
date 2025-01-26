package com.example.s5app.use_case

import com.example.s5app.network.ApiResult
import com.example.s5app.network.CupidApiRepository
import com.example.s5app.network.GetGivenPhotoCommentParams

class GetCommentsForGivenPhotoUseCase(
    private val repository: CupidApiRepository
) {
    suspend operator fun invoke(userToken: String, imageId: String, firstCommentIndex: Int, lastCommentIndex: Int): ApiResult<List<GetGivenPhotoCommentParams>> {
        return repository.getCommentsForGivenPhoto(userToken, imageId, firstCommentIndex, lastCommentIndex)
    }
}