package com.example.s5app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.s5app.event.MainScreenEvent
import com.example.s5app.network.ApiResult
import com.example.s5app.network.CreateEventParams
import com.example.s5app.network.GetEventParams
import com.example.s5app.use_case.EventUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val eventUseCases: EventUseCases
): ViewModel() {

    private val _getEventResponse = MutableStateFlow<ApiResult<GetEventParams>>(ApiResult.Init)
    val getEventResponse: StateFlow<ApiResult<GetEventParams>> = _getEventResponse

    private val _createEventResponse = MutableStateFlow<ApiResult<CreateEventParams>>(ApiResult.Init)
    val createEventResponse: StateFlow<ApiResult<CreateEventParams>> = _createEventResponse

    fun onEvent(event: MainScreenEvent) {
        when(event) {
            is MainScreenEvent.GetEvent -> {
                viewModelScope.launch {
                    _getEventResponse.value = eventUseCases.getEvent(event.userToken)
                }
            }
            is MainScreenEvent.CreateEvent -> {
                viewModelScope.launch {
                    _createEventResponse.value = eventUseCases.createEvent(event.eventName)
                }
            }
            is MainScreenEvent.ClearCurrentEventToken -> {
                _getEventResponse.value = ApiResult.Init
            }
            is MainScreenEvent.ClearPreviouslyCreatedEventName -> {
                _createEventResponse.value = ApiResult.Init
            }
        }
    }

}


