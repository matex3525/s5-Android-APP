package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class GetEventParams(
    @SerializedName("event_name")
    val eventName: String
)