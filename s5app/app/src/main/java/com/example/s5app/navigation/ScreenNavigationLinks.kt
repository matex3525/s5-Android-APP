package com.example.s5app.navigation

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.Serializable

@Serializable
object MainScreen

@Serializable
object AlbumScreen

@Serializable
data class AlbumImageDetailsScreen(
    val imageByteArray: String
)