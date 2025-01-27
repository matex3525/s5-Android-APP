package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class CreateEventRequest(
    @SerializedName("event_name")
    val eventName: String
)

data class CreateEventParams(
    @SerializedName("user_token")
    val userToken: String,
    @SerializedName("admin_token")
    val adminToken: String
)