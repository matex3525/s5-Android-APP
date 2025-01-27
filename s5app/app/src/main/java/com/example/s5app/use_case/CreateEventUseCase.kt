package com.example.s5app.use_case

import com.example.s5app.network.ApiResult
import com.example.s5app.network.CreateEventParams
import com.example.s5app.network.CupidApiRepository

class CreateEventUseCase(
    private val repository: CupidApiRepository
) {
    suspend operator fun invoke(eventName: String): ApiResult<CreateEventParams> {
        return repository.createEvent(eventName)
    }
}