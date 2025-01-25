package com.example.s5app.network

import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path

interface CupidApiService {
    @POST("/v0/event")
    suspend fun createEvent(@Body request: CreateEventRequest): ApiResponse<CreateEventParams>
    @GET("v0/event/{user_token}")
    suspend fun getEvent(@Path("user_token") userToken: String): ApiResponse<GetEventParams>
    @GET("/v0/event/{user_token}/image/byindices/{first_image_index}/{last_image_index}")
    suspend fun getPhotosForGivenEvent(@Path("user_token") userToken: String, @Path("first_image_index") firstImageIndex: Int, @Path("last_image_index") lastImageIndex: Int): ApiResponse<GetGivenEventPhotosParams>
//    @POST("/v0/event/{user_token}/image")
//    suspend fun addPhotoToEvent(@Path("user_token") userToken: String, @Body request: AddPhotoToEventRequest): ApiResponse<AddPhotoToEventParams>
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