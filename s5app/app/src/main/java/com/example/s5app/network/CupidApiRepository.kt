package com.example.s5app.network

interface CupidApiRepository {
    suspend fun createEvent(eventName: String): ApiResult<CreateEventParams>
    suspend fun getEvent(userToken: String): ApiResult<GetEventParams>
    suspend fun getPhotosForGivenEvent(userToken: String, firstImageIndex: Int, lastImageIndex: Int): ApiResult<GetGivenEventPhotosParams>
}