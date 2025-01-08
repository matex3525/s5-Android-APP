package com.example.s5app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val IP: String = "192.168.0.224"
private const val BASE_URL = "http://$IP:8080"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface CupidApiService {
    @POST("/v0/event")
    suspend fun createEvent(@Body request: CreateEventRequest): CreateEventResponse
}

object CupidApi {
    val retrofitService: CupidApiService by lazy {
        retrofit.create(CupidApiService::class.java)
    }
}

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}