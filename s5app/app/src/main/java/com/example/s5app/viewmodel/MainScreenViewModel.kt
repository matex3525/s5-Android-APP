package com.example.s5app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.s5app.network.CreateEventRequest
import com.example.s5app.network.CupidApi
import kotlinx.coroutines.launch
import java.io.IOException

class MainScreenViewModel: ViewModel() {
    fun createEvent(eventName: String) {
        viewModelScope.launch {
            try {
                val request = CreateEventRequest(
                    eventName
                )
                val response = CupidApi.retrofitService.createEvent(request)
                Log.d("CupidApi", "Success: ${response.success}, user_token: ${response.params.userToken}, admin_token: ${response.params.adminToken}")
            } catch (e: IOException) {
                Log.e("CupidApi", "Failure: ${e.message}")
            }
        }
    }
}


