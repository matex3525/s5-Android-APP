package com.example.s5app.network

import android.util.Log
import retrofit2.HttpException
import java.io.IOException

class CupidApiRepositoryImpl(
    private val api: CupidApiService
) : CupidApiRepository {
    override suspend fun createEvent(eventName: String): ApiResult<CreateEventParams> {
        return try {
            // Tworzenie żądania
            val request = CreateEventRequest(eventName)

            // Wysyłanie żądania do API
            val response = api.createEvent(request)

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

    override suspend fun getEvent(userToken: String): ApiResult<GetEventParams> {
        return try {
            // Wysyłamy żądanie GET do API za pomocą Retrofit
            val response = api.getEvent(userToken)

            // Sprawdzamy, czy operacja zakończyła się sukcesem
            if (response.success) {
                Log.d("CupidApi", "Success: ${response.success}, event: ${response.params.eventName}")

                // Zwrócenie obiektu odpowiedzi w przypadku sukcesu
                ApiResult.Success(response.params)
            } else {
                Log.e("CupidApi", "API failure: success=false")
                ApiResult.Error("API failure: success=false")
            }

        } catch (e: IOException) {
            // Obsługa błędów komunikacji (np. problemy z siecią)
            Log.e("CupidApi", "Network failure: ${e.message}")
            ApiResult.Error("Network failure: ${e.message}")
        } catch (e: HttpException) {
            // Obsługa błędów HTTP (np. 404, 401)
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

    override suspend fun checkAdminTokenForEvent(
        userToken: String,
        adminToken: String
    ): ApiResult<CheckAdminTokenForEventParams> {
        return try {
            // Wysyłamy żądanie POST do API za pomocą Retrofit
            val response = api.checkAdminTokenForEvent(userToken, CheckAdminTokenForEventRequest(adminToken))

            // Sprawdzamy, czy operacja zakończyła się sukcesem
            if (response.success) {
                Log.d("CupidApi", "Success: ${response.success}, event: ${response.params.eventName}")

                // Zwrócenie obiektu odpowiedzi w przypadku sukcesu
                ApiResult.Success(response.params)
            } else {
                Log.e("CupidApi", "API failure: success=false")
                ApiResult.Error("API failure: success=false")
            }

        } catch (e: IOException) {
            // Obsługa błędów komunikacji (np. problemy z siecią)
            Log.e("CupidApi", "Network failure: ${e.message}")
            ApiResult.Error("Network failure: ${e.message}")
        } catch (e: HttpException) {
            // Obsługa błędów HTTP (np. 404, 401)
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

    override suspend fun getPhotosForGivenEvent(
        userToken: String,
        firstImageIndex: Int,
        lastImageIndex: Int
    ): ApiResult<List<GetGivenEventPhotoParams>> {
        return try {
            // Wysyłamy żądanie GET do API za pomocą Retrofit
            val response = api.getPhotosForGivenEvent(userToken, firstImageIndex, lastImageIndex)

            // Sprawdzamy, czy operacja zakończyła się sukcesem
            if (response.success) {
                Log.d("CupidApi", "Success: ${response.success}, event: ${response.params}")

                // Zwrócenie obiektu odpowiedzi w przypadku sukcesu
                ApiResult.Success(response.params)
            } else {
                Log.e("CupidApi", "API failure: success=false")
                ApiResult.Error("API failure: success=false")
            }

        } catch (e: IOException) {
            // Obsługa błędów komunikacji (np. problemy z siecią)
            Log.e("CupidApi", "Network failure: ${e.message}")
            ApiResult.Error("Network failure: ${e.message}")
        } catch (e: HttpException) {
            // Obsługa błędów HTTP (np. 404, 401)
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

    override suspend fun addPhotoToEvent(
        userToken: String,
        request: AddPhotoToEventRequest
    ): ApiResult<AddPhotoToEventParams> {
        return try {
            // Wysyłamy żądanie POST do API za pomocą Retrofit
            val response = api.addPhotoToEvent(userToken, request)

            // Sprawdzamy, czy operacja zakończyła się sukcesem
            if (response.success) {
                Log.d("CupidApi", "Success: ${response.success}, event: ${response.params}")

                // Zwrócenie obiektu odpowiedzi w przypadku sukcesu
                ApiResult.Success(response.params)
            } else {
                Log.e("CupidApi", "API failure: success=false")
                ApiResult.Error("API failure: success=false")
            }

        } catch (e: IOException) {
            // Obsługa błędów komunikacji (np. problemy z siecią)
            Log.e("CupidApi", "Network failure: ${e.message}")
            ApiResult.Error("Network failure: ${e.message}")
        } catch (e: HttpException) {
            // Obsługa błędów HTTP (np. 404, 401)
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

    override suspend fun getCommentsForGivenPhoto(
        userToken: String,
        imageId: String,
        firstCommentIndex: Int,
        lastCommentIndex: Int
    ): ApiResult<List<GetGivenPhotoCommentParams>> {
        return try {
            // Wysyłamy żądanie GET do API za pomocą Retrofit
            val response = api.getCommentsForGivenPhoto(userToken, imageId, firstCommentIndex, lastCommentIndex)

            // Sprawdzamy, czy operacja zakończyła się sukcesem
            if (response.success) {
                Log.d("CupidApi", "Success: ${response.success}, event: ${response.params}")

                // Zwrócenie obiektu odpowiedzi w przypadku sukcesu
                ApiResult.Success(response.params)
            } else {
                Log.e("CupidApi", "API failure: success=false")
                ApiResult.Error("API failure: success=false")
            }

        } catch (e: IOException) {
            // Obsługa błędów komunikacji (np. problemy z siecią)
            Log.e("CupidApi", "Network failure: ${e.message}")
            ApiResult.Error("Network failure: ${e.message}")
        } catch (e: HttpException) {
            // Obsługa błędów HTTP (np. 404, 401)
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

    override suspend fun addCommentToPhoto(
        userToken: String,
        imageId: String,
        request: AddCommentToPhotoRequest
    ): ApiResult<AddCommentToPhotoParams> {
        return try {
            // Wysyłamy żądanie POST do API za pomocą Retrofit
            val response = api.addCommentToPhoto(userToken, imageId, request)

            // Sprawdzamy, czy operacja zakończyła się sukcesem
            if (response.success) {
                Log.d("CupidApi", "Success: ${response.success}, event: ${response.params}")

                // Zwrócenie obiektu odpowiedzi w przypadku sukcesu
                ApiResult.Success(response.params)
            } else {
                Log.e("CupidApi", "API failure: success=false")
                ApiResult.Error("API failure: success=false")
            }

        } catch (e: IOException) {
            // Obsługa błędów komunikacji (np. problemy z siecią)
            Log.e("CupidApi", "Network failure: ${e.message}")
            ApiResult.Error("Network failure: ${e.message}")
        } catch (e: HttpException) {
            // Obsługa błędów HTTP (np. 404, 401)
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

    override suspend fun deleteCommentFromPhoto(
        userToken: String,
        imageId: String,
        commentId: String,
        adminToken: String
    ) {
        try {
            // Wysyłamy żądanie DELETE do API za pomocą Retrofit
            api.deleteCommentFromPhoto(userToken, imageId, commentId, adminToken)
        } catch (e: IOException) {
            // Obsługa błędów komunikacji (np. problemy z siecią)
            Log.e("CupidApi", "Network failure: ${e.message}")
        } catch (e: HttpException) {
            // Obsługa błędów HTTP (np. 404, 401)
            when (e.code()) {
                401 -> Log.e("CupidApi", "Unauthorized: ${e.message()}")
                404 -> Log.e("CupidApi", "Not Found: ${e.message()}")
                else -> Log.e("CupidApi", "HTTP Error: ${e.code()}, ${e.message()}")
            }
        } catch (e: Exception) {
            // Obsługa innych wyjątków
            Log.e("CupidApi", "Unexpected failure: ${e.message}")
        }
    }
}