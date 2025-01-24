package com.example.s5app.event

sealed class MainScreenEvent {
    data class GetEvent(val userToken: String): MainScreenEvent()
    data class CreateEvent(val eventName: String): MainScreenEvent()
    object ClearCurrentEventToken: MainScreenEvent()
    object ClearPreviouslyCreatedEventName: MainScreenEvent()
}