package com.example.s5app.viewmodel

import android.util.Log
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConnectivityViewModel : ViewModel() {

    // Przechowujemy stan internetu (prawda/fałsz)
    private val _isInternetAvailable = MutableStateFlow(true)
    val isInternetAvailable: StateFlow<Boolean> get() = _isInternetAvailable

    private val checkIntervalMillis: Long = 5000 // Interwał: 5 sekund

    init {
        // Uruchamiamy cykliczne sprawdzanie internetu
        monitorInternetConnection()
    }

    private fun monitorInternetConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val result = isInternetAvailable() // Funkcja ping
                if (result) {
                    Log.d("ConnectivityViewModel", "Internet is available")
                } else {
                    Log.e("ConnectivityViewModel", "Internet is not available")
                }
                _isInternetAvailable.emit(result)  // Emitujemy nowy stan
                delay(checkIntervalMillis)        // Czekamy na kolejny cykl
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Coroutine cleanup: anulujemy cykliczne sprawdzanie internetu
        viewModelScope.cancel()
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500) // Google DNS (timeout: 1500ms)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }
}
