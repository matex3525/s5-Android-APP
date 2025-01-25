package com.example.s5app.use_case

import com.example.s5app.network.AddPhotoToEventParams
import com.example.s5app.network.AddPhotoToEventRequest
import com.example.s5app.network.ApiResult
import com.example.s5app.network.CupidApiRepository

class AddPhotoToEventUseCase(
    private val repository: CupidApiRepository
) {
    suspend operator fun invoke(userToken: String, request: AddPhotoToEventRequest): ApiResult<AddPhotoToEventParams> {
        return repository.addPhotoToEvent(userToken, request)
    }
}
