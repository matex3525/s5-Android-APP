package com.example.s5app.model

import kotlinx.serialization.Serializable

@Serializable
data class AlbumTokens(
    val eventName: String,
    val userToken: String,
    val adminToken: String? = null
)
