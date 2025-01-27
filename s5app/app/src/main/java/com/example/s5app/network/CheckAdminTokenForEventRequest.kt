package com.example.s5app.network

import com.google.gson.annotations.SerializedName

data class CheckAdminTokenForEventRequest(
    @SerializedName("admin_token")
    val adminToken: String
)


