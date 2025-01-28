package com.example.s5app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.ExperimentalEncodingApi

object BitmapUtil {
    @OptIn(ExperimentalEncodingApi::class)
    fun bitmapToBase64ARGB(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return base64
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun base64ARGBToBitmap(base64Pixels: String): Bitmap {
        val decodedString = Base64.decode(base64Pixels, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}