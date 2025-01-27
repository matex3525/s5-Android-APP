package com.example.s5app.event

sealed class AlbumImageDetailsScreenEvent {
    object GetAllComments : AlbumImageDetailsScreenEvent()
    data class PostComment(val comment: String) : AlbumImageDetailsScreenEvent()
    data class  DeleteComment(val commentId: String) : AlbumImageDetailsScreenEvent()
}