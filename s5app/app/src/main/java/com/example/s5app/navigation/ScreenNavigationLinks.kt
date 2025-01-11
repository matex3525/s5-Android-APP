package com.example.s5app.navigation

import kotlinx.serialization.Serializable

@Serializable
object MainScreen

@Serializable
data class AlbumScreen(
    val userToken: String,
    val eventName: String
)

@Serializable
data class AlbumImageDetailsScreen(
    val imageByteArray: String
)