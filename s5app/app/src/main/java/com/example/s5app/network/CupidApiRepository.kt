package com.example.s5app.network

import retrofit2.http.Body
import retrofit2.http.Path

interface CupidApiRepository {
    suspend fun createEvent(eventName: String): ApiResult<CreateEventParams>
    suspend fun getEvent(userToken: String): ApiResult<GetEventParams>
    suspend fun checkAdminTokenForEvent(userToken: String, adminToken: String): ApiResult<CheckAdminTokenForEventParams>
    suspend fun getPhotosForGivenEvent(userToken: String, firstImageIndex: Int, lastImageIndex: Int): ApiResult<List<GetGivenEventPhotoParams>>
    suspend fun addPhotoToEvent(userToken: String, request: AddPhotoToEventRequest): ApiResult<AddPhotoToEventParams>
    suspend fun getCommentsForGivenPhoto(userToken: String, imageId: String, firstCommentIndex: Int, lastCommentIndex: Int): ApiResult<List<GetGivenPhotoCommentParams>>
    suspend fun addCommentToPhoto(userToken: String, imageId: String, request: AddCommentToPhotoRequest): ApiResult<AddCommentToPhotoParams>
    suspend fun deleteCommentFromPhoto(userToken: String, imageId: String, commentId: String, adminToken: String)
}