package com.example.s5app.event

sealed class MainScreenEvent {
    data class GetEvent(val userToken: String): MainScreenEvent()
    data class CreateEvent(val eventName: String): MainScreenEvent()
    data class CheckAdminToken(val userToken: String, val adminToken: String): MainScreenEvent()
    object ClearCurrentEventToken: MainScreenEvent()
    object ClearCurrentAdminToken: MainScreenEvent()
    object ClearPreviouslyCreatedEventName: MainScreenEvent()
}