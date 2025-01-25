package com.example.s5app.use_case

import com.example.s5app.network.ApiResult
import com.example.s5app.network.CupidApiRepository
import com.example.s5app.network.GetGivenEventPhotosParams

class GetPhotosForGivenEventUseCase(
    private val repository: CupidApiRepository
) {
    suspend operator fun invoke(userToken: String, firstImageIndex: Int, lastImageIndex: Int): ApiResult<GetGivenEventPhotosParams> {
        return repository.getPhotosForGivenEvent(userToken, firstImageIndex, lastImageIndex)
    }
}