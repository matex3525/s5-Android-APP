package com.example.s5app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.s5app.network.ConnectivityService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    private val connectivityService: ConnectivityService
) : ViewModel() {

    val isInternetAvailable: StateFlow<Boolean> = connectivityService.isInternetAvailable

    init {
        connectivityService.startMonitoring()
    }

    override fun onCleared() {
        super.onCleared()
        connectivityService.stopMonitoring()
    }
}
