package com.example.s5app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import java.util.Date

class AlbumImageDetailsScreenViewModel(
    bitmapSource: ImageBitmap?,
    timeOfCreation: Date,
    comments: List<String>
) : ViewModel() {
    var bitmapSource by mutableStateOf(bitmapSource)
        private set
    val timeOfCreation by mutableStateOf(timeOfCreation)
    var comments = comments.toMutableStateList()
        private set

    fun addComment(comment: String) {
        comments.add(comment)
    }
}