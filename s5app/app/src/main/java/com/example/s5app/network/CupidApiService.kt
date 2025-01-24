package com.example.s5app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL =
    "http://10.0.2.2:8080"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface CupidApiService {
    @POST("/v0/event")
    suspend fun createEvent(@Body request: CreateEventRequest): ApiResponse<CreateEventParams>

    @GET("v0/event/{user_token}")
    suspend fun getEvent(@Path("user_token") userToken: String): ApiResponse<GetEventParams>
    @GET("/v0/event/{user_token}/image/byindices/{first_image_index}/{last_image_index}")
    suspend fun getPhotosForGivenEvent(@Path("user_token") userToken: String, @Path("first_image_index") firstImageIndex: Int, @Path("last_image_index") lastImageIndex: Int): ApiResponse<GetGivenEventPhotosParams>
}

interface CupidApiRepository {
    suspend fun createEvent(eventName: String): ApiResult<CreateEventParams>
    suspend fun getEvent(userToken: String): ApiResult<GetEventParams>
//    suspend fun getPhotosForGivenEvent(userToken: String, firstImageIndex: Int, lastImageIndex: Int): ApiResult<GetGivenEventPhotosParams>
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
    object Init : ApiResult<Nothing>()
}

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val params: T
)