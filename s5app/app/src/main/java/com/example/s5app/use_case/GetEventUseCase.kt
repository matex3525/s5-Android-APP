package com.example.s5app.use_case

import com.example.s5app.network.ApiResponse
import com.example.s5app.network.ApiResult
import com.example.s5app.network.CupidApiRepository
import com.example.s5app.network.CupidApiService
import com.example.s5app.network.GetEventParams

class GetEventUseCase(
    private val repository: CupidApiRepository
) {
    suspend operator fun invoke(userToken: String): ApiResult<GetEventParams> {
        return repository.getEvent(userToken)
    }
}