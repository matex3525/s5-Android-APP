package com.example.s5app

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.concurrent.thread
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun apiErrorCodeToString(code: Int) = when(code) {
    0 -> "No such event."
    1 -> "Incorrect admin token."
    2 -> "String too long."
    3 -> "Inappropriate string."
    4 -> "Internal error."
    5 -> "Image width invalid."
    6 -> "Image height invalid."
    else -> "Unknown error :("
}

data class MainViewModelImage(
    var imageId: String,
    var title: String,
    var image: ImageBitmap
)

data class MainViewModelState(
    var currentUserToken: String = "",
    var currentAdminToken: String = ""
)

class MainViewModel : ViewModel() {
    private val serverAddress = "192.168.0.224" //"10.10.17.131"

    private val _state = MutableStateFlow(MainViewModelState())
    val state = _state.asStateFlow()

    fun leaveCurrentEvent() {
        //_state.value?.images?.clear()
        _state.value.currentUserToken = ""
        _state.value.currentAdminToken = ""
    }

    fun enterEvent(userToken: String,adminToken: String,onError: (msg: String) -> Unit,onSuccess: () -> Unit) {
        networkThread("enterEvent",onError) {
            val response = urlRequest(HttpMethod.Post,"http://$serverAddress:8080/auth/$userToken",mapOf(Pair("admin_token",adminToken)))
            if(!(response["success"] as Boolean)) {
                return@networkThread onError(apiErrorCodeToString(response["params"] as Int))
            }
            _state.value.currentUserToken = userToken
            _state.value.currentAdminToken = adminToken
            runOnMainThread(onSuccess)
        }
    }

    fun createEvent(eventName: String,onError: (msg: String) -> Unit,onSuccess: () -> Unit) {
        networkThread("createEvent",onError) {
            val response = urlRequest(HttpMethod.Post,"http://$serverAddress:8080/event",mapOf(Pair("event_name",eventName)))
            if(!(response["success"] as Boolean)) {
                return@networkThread onError(apiErrorCodeToString(response["params"] as Int))
            }
            val params = response["params"] as Map<*,*>
            _state.value.currentUserToken = params["user_token"] as String
            _state.value.currentAdminToken = params["admin_token"] as String
            runOnMainThread(onSuccess)
        }
    }

    fun deleteCurrentEvent(onError: (msg: String) -> Unit,onSuccess: () -> Unit) {
        networkThread("deleteCurrentEvent",onError) {
            val response = urlRequest(HttpMethod.Delete,"http://$serverAddress:8080/event/${_state.value.currentUserToken}",mapOf(Pair("admin_token",_state.value.currentAdminToken)))
            if(!(response["success"] as Boolean)) {
                return@networkThread onError(apiErrorCodeToString(response["params"] as Int))
            }
            leaveCurrentEvent()
            runOnMainThread(onSuccess)
        }
    }

    fun getImageCount(onError: (msg: String) -> Unit = {},onSuccess: (count: Int) -> Unit) {
        networkThread("downloadImages",onError) {
            val response = urlRequest(HttpMethod.Get,"http://$serverAddress:8080/getimagecount/${_state.value.currentUserToken}")
            if(!(response["success"] as Boolean)) {
                return@networkThread onError(apiErrorCodeToString(response["params"] as Int))
            }
            runOnMainThread {
                onSuccess(response["params"] as Int)
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun downloadImages(onError: (msg: String) -> Unit = {},onImageLoaded: (image: MainViewModelImage,index: Int) -> Unit) {
        networkThread("downloadImages",onError) {
            val response = urlRequest(HttpMethod.Get,"http://$serverAddress:8080/images/${_state.value.currentUserToken}/0-0")
            if(!(response["success"] as Boolean)) {
                return@networkThread onError(apiErrorCodeToString(response["params"] as Int))
            }
            val params = response["params"] as List<*>
            for(i in params.indices) {
                val image = params[i] as Map<*,*>
                val decodedPixels = Base64.Default.decode(image["pixels"] as String)
                val bitmap = Bitmap.createBitmap(
                    rgbByteArrayToArgbIntArray(decodedPixels),
                    (image["width"] as String).toInt(),
                    (image["height"] as String).toInt(),
                    Bitmap.Config.ARGB_8888
                ).asImageBitmap()
                runOnMainThread {
                    onImageLoaded(MainViewModelImage(
                        imageId = image["image_id"] as String,
                        title = image["title"] as String,
                        image = bitmap
                    ),i)
                }
            }
        }
    }

    fun getCurrentEventTitle(onError: (msg: String) -> Unit = {},onSuccess: (title: String) -> Unit) {
        networkThread("getCurrentEventTitle",{}) {
            val response = urlRequest(HttpMethod.Get,"http://$serverAddress:8080/event/${_state.value.currentUserToken}")
            if(!(response["success"] as Boolean)) {
                return@networkThread onError(apiErrorCodeToString(response["params"] as Int))
            }
            val eventName = (response["params"] as Map<*,*>)["event_name"] as String
            runOnMainThread { onSuccess(eventName) }
        }
    }

    private fun networkThread(functionName: String,onError: (msg: String) -> Unit,action: () -> Unit) = thread {
        try {
            action()
        }
        catch(error: SocketTimeoutException) {
            Log.d("MainViewModel.$functionName","${error.message!!}\n${error.stackTraceToString()}")
            onError("Server unavailable, try again later.")
        }
        catch(error: IOException) {
            Log.d("MainViewModel.$functionName","IOException: \"${error.message!!}\\n${error.stackTraceToString()}\"")
            onError("I/O error, try again later.")
        }
        catch(error: Exception) {
            Log.d("MainViewModel.$functionName","Exception: \"${error.message!!}\\n${error.stackTraceToString()}\"")
            onError("Internal error, try again later.")
        }
    }
}