package com.example.s5app.network

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _isInternetAvailable = MutableStateFlow(false)
    val isInternetAvailable: StateFlow<Boolean> get() = _isInternetAvailable

    private val checkIntervalMillis: Long = 5000 // Interwał: 5 sekund
    private var monitorJob: Job? = null

    // Uruchomienie monitorowania internetu
    fun startMonitoring() {
        if (monitorJob == null) {
            monitorJob = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    val result = isInternetAvailableNow()
                    if (result) {
                        Log.d("ConnectivityService", "Internet is available")
                    } else {
                        Log.e("ConnectivityService", "Internet is not available")
                    }
                    _isInternetAvailable.emit(result)
                    delay(checkIntervalMillis)
                }
            }
        }
    }

    fun stopMonitoring() {
        monitorJob?.cancel()
        monitorJob = null
    }

    private fun isInternetAvailableNow(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }
}