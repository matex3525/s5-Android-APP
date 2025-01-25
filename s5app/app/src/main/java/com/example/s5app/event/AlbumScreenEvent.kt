package com.example.s5app.event

sealed class AlbumScreenEvent {
    data class GetAllPhotosForGivenEvent(val eventToken: String) : AlbumScreenEvent()
}