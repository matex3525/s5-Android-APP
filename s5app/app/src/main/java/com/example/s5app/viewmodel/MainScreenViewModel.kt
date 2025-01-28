package com.example.s5app.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.s5app.event.MainScreenEvent
import com.example.s5app.model.AlbumImage
import com.example.s5app.model.AlbumTokens
import com.example.s5app.network.ApiResult
import com.example.s5app.network.CheckAdminTokenForEventParams
import com.example.s5app.network.CreateEventParams
import com.example.s5app.network.GetEventParams
import com.example.s5app.use_case.EventUseCases
import com.example.s5app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
    private val dataStoreManager: DataStoreManager
): ViewModel() {

    private val _getEventResponse = MutableStateFlow<ApiResult<GetEventParams>>(ApiResult.Init)
    val getEventResponse: StateFlow<ApiResult<GetEventParams>> = _getEventResponse

    private val _checkAdminTokenResponse = MutableStateFlow<ApiResult<CheckAdminTokenForEventParams>>(ApiResult.Init)
    val checkAdminTokenResponse: StateFlow<ApiResult<CheckAdminTokenForEventParams>> = _checkAdminTokenResponse

    private val _createEventResponse = MutableStateFlow<ApiResult<CreateEventParams>>(ApiResult.Init)
    val createEventResponse: StateFlow<ApiResult<CreateEventParams>> = _createEventResponse

    val albumTokensFlow: StateFlow<List<AlbumTokens>> = dataStoreManager.albumTokensFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun addAlbumToken(newToken: AlbumTokens) {
        viewModelScope.launch {
            dataStoreManager.addOrUpdateAlbumToken(newToken)
        }
    }

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
            is MainScreenEvent.CheckAdminToken -> {
                viewModelScope.launch {
                    _checkAdminTokenResponse.value = eventUseCases.checkAdminTokenForEvent(event.userToken, event.adminToken)
                }
            }
            is MainScreenEvent.ClearCurrentEventToken -> {
                _getEventResponse.value = ApiResult.Init
            }
            is MainScreenEvent.ClearCurrentAdminToken -> {
                _checkAdminTokenResponse.value = ApiResult.Init
            }
            is MainScreenEvent.ClearPreviouslyCreatedEventName -> {
                _createEventResponse.value = ApiResult.Init
            }
        }
    }

}


