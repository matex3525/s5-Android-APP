package com.example.s5app.use_case

data class EventUseCases(
    val getEvent: GetEventUseCase,
    val createEvent: CreateEventUseCase,
    val getPhotosForGivenEvent: GetPhotosForGivenEventUseCase
)

