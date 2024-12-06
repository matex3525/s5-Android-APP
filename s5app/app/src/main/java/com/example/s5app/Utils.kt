package com.example.s5app

import android.graphics.Color
import java.net.URL
import android.os.Looper
import android.os.Handler
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection

fun rgbByteArrayToArgbIntArray(param: ByteArray): IntArray {
    val pixels = IntArray(param.size / 3)
    var pixelIndex = 0
    for(i in param.indices step 3) {
        pixels[pixelIndex] = Color.argb(255,param[i + 0].toInt(),param[i + 1].toInt(),param[i + 2].toInt())
        pixelIndex += 1
    }
    return pixels
}

fun runOnMainThread(action: () -> Unit) {
    val mainLooper = Looper.getMainLooper()
    if(mainLooper.thread != Thread.currentThread()) {
        val handler = Handler(mainLooper)
        handler.post(action)
    }
    else action()
}

fun JSONObject.toMap(): Map<String,*> = keys().asSequence().associateWith { key ->
    when(val value = this[key]) {
        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        is JSONArray -> {
            val map = (0 until value.length()).associate { Pair(it.toString(),value[it]) }
            JSONObject(map).toMap().values.toList()
        }
        else -> value
    }
}

private fun HttpURLConnection.setJson(json: Map<String,*>? = null) {
    if(json == null) return
    doOutput = true
    setRequestProperty("Content-Type","application/json")
    setRequestProperty("Accept","application/json")
    BufferedWriter(OutputStreamWriter(outputStream)).use { it.write(JSONObject(json).toString()) }
}

enum class HttpMethod {
    Get,Post,Delete;
    override fun toString() = when(this) {
        Get -> "GET"
        Post -> "POST"
        Delete -> "DELETE"
        else -> throw EnumConstantNotPresentException(this::class.java,"")
    }
}

fun urlRequest(method: HttpMethod,url: String,json: Map<String,*>? = null): Map<String,*>  {
    val conn = URL(url).openConnection() as HttpURLConnection
    conn.connectTimeout = 5000
    conn.requestMethod = method.toString()
    conn.setJson(json)
    conn.connect()
    return try { JSONObject(conn.inputStream.reader().use { it.readText() }).toMap() }
    finally { conn.disconnect() }
}