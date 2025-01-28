package com.example.s5app.use_case

data class EventUseCases(
    val getEvent: GetEventUseCase,
    val createEvent: CreateEventUseCase,
    val checkAdminTokenForEvent: CheckAdminTokenForEventUseCase,
    val getPhotosForGivenEvent: GetPhotosForGivenEventUseCase,
    val addPhotoToEvent: AddPhotoToEventUseCase,
    val getCommentsForGivenPhoto: GetCommentsForGivenPhotoUseCase,
    val addCommentToPhoto: AddCommentToPhotoUseCase,
    val deleteCommentFromPhoto: DeleteCommentFromPhotoUseCase,
    val getPDFForEvent: GetPDFForEventUseCase
)

