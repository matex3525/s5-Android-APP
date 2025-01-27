package com.example.s5app.use_case

import com.example.s5app.network.ApiResult
import com.example.s5app.network.CheckAdminTokenForEventParams
import com.example.s5app.network.CupidApiRepository

class CheckAdminTokenForEventUseCase(
    private val repository: CupidApiRepository
) {
    suspend operator fun invoke(userToken: String, adminToken: String): ApiResult<CheckAdminTokenForEventParams> {
        return repository.checkAdminTokenForEvent(userToken, adminToken)
    }
}