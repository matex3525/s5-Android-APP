package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class CheckAdminTokenForEventParams(
    @SerializedName("event_name")
    val eventName: String
)


