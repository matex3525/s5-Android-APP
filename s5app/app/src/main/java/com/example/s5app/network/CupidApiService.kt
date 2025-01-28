package com.example.s5app.network

import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.serialization.Serializable
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CupidApiService {
    @POST("/v0/event")
    suspend fun createEvent(@Body request: CreateEventRequest): ApiResponse<CreateEventParams>
    @GET("v0/event/{user_token}")
    suspend fun getEvent(@Path("user_token") userToken: String): ApiResponse<GetEventParams>
    @POST("/v0/event/{user_token}/check")
    suspend fun checkAdminTokenForEvent(@Path("user_token") userToken: String, @Body request: CheckAdminTokenForEventRequest): ApiResponse<CheckAdminTokenForEventParams>
    @GET("/v0/event/{user_token}/image/byindices/{first_image_index}/{last_image_index}")
    suspend fun getPhotosForGivenEvent(@Path("user_token") userToken: String, @Path("first_image_index") firstImageIndex: Int, @Path("last_image_index") lastImageIndex: Int): ApiResponse<List<GetGivenEventPhotoParams>>
    @POST("/v0/event/{user_token}/image")
    suspend fun addPhotoToEvent(@Path("user_token") userToken: String, @Body request: AddPhotoToEventRequest): ApiResponse<AddPhotoToEventParams>
    @GET("/v0/event/{user_token}/image/byid/{image_id}/comment/byindices/{first_comment_index}/{last_comment_index}")
    suspend fun getCommentsForGivenPhoto(@Path("user_token") userToken: String, @Path("image_id") imageId: String, @Path("first_comment_index") firstCommentIndex: Int, @Path("last_comment_index") lastCommentIndex: Int): ApiResponse<List<GetGivenPhotoCommentParams>>
    @POST("/v0/event/{user_token}/image/byid/{image_id}/comment")
    suspend fun addCommentToPhoto(@Path("user_token") userToken: String, @Path("image_id") imageId: String, @Body request: AddCommentToPhotoRequest): ApiResponse<AddCommentToPhotoParams>
    @DELETE("/v0/event/{user_token}/image/byid/{image_id}/comment/byid/{comment_id}")
    suspend fun deleteCommentFromPhoto(@Path("user_token") userToken: String, @Path("image_id") imageId: String, @Path("comment_id") commentId: String, @Query("admin_token") adminToken: String)
    @GET("/v0/event/{user_token}/PDF")
    suspend fun getPDFForEvent(@Path("user_token") userToken: String): ByteArray
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