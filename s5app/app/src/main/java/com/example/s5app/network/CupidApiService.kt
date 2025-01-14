package com.example.s5app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.serialization.Serializable

private const val BASE_URL =
    "http://10.0.2.2:8080"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface CupidApiService {
    @POST("/v0/event")
    suspend fun createEvent(@Body request: CreateEventRequest): ApiResponse<CreateEventParams>
}

object CupidApi {
    val retrofitService: CupidApiService by lazy {
        retrofit.create(CupidApiService::class.java)
    }
}

// Typ wynikowy (Success/Error)
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val params: T
)