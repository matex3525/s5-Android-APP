package com.example.s5app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.s5app.network.ApiResult
import com.example.s5app.network.CreateEventParams
import com.example.s5app.network.CreateEventRequest
import com.example.s5app.network.CupidApi
import retrofit2.HttpException
import java.io.IOException

class MainScreenViewModel: ViewModel() {
    suspend fun createEvent(eventName: String): ApiResult<CreateEventParams> {
        return try {
            // Tworzenie żądania
            val request = CreateEventRequest(eventName)

            // Wysyłanie żądania do API
            val response = CupidApi.retrofitService.createEvent(request)

            // Sprawdzamy, czy operacja zakończyła się sukcesem
            if (response.success) {
                Log.d("CupidApi", "Success: ${response.success}, user_token: ${response.params.userToken}, admin_token: ${response.params.adminToken}")

                // Zwrot obiektu params z odpowiedzi
                ApiResult.Success(response.params)
            } else {
                Log.e("CupidApi", "API failure: success=false")
                ApiResult.Error("API failure: success=false") // Jeśli API zwróci odpowiedź z success=false, zwracamy null
            }

        } catch (e: IOException) {
            // Obsługa błędów komunikacji (brak internetu, sieć rozłączona)
            Log.e("CupidApi", "Network failure: ${e.message}")
            ApiResult.Error("Network failure: ${e.message}")
        } catch (e: HttpException) {
            // Obsługa błędów HTTP
            when (e.code()) {
                401 -> Log.e("CupidApi", "Unauthorized: ${e.message()}")
                404 -> Log.e("CupidApi", "Not Found: ${e.message()}")
                else -> Log.e("CupidApi", "HTTP Error: ${e.code()}, ${e.message()}")
            }
            ApiResult.Error(e.message.toString())
        } catch (e: Exception) {
            // Obsługa innych wyjątków
            Log.e("CupidApi", "Unexpected failure: ${e.message}")
            ApiResult.Error("Unexpected failure: ${e.message}")
        }
    }
}


