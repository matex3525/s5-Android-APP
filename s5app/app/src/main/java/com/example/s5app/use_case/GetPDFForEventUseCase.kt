package com.example.s5app.use_case

import com.example.s5app.network.CupidApiRepository

class GetPDFForEventUseCase(
    private val repository: CupidApiRepository
) {
    suspend operator fun invoke(userToken: String): ByteArray {
        return repository.getPDFForEvent(userToken)
    }
}