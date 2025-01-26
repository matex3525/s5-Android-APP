package com.example.s5app.use_case

data class EventUseCases(
    val getEvent: GetEventUseCase,
    val createEvent: CreateEventUseCase,
    val getPhotosForGivenEvent: GetPhotosForGivenEventUseCase,
    val addPhotoToEvent: AddPhotoToEventUseCase,
    val getCommentsForGivenPhoto: GetCommentsForGivenPhotoUseCase,
    val addCommentToPhoto: AddCommentToPhotoUseCase
)

