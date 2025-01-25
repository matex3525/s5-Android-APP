package com.example.s5app.util

import android.graphics.Bitmap
import java.nio.ByteBuffer
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object BitmapUtil {
    @OptIn(ExperimentalEncodingApi::class)
    fun bitmapToBase64ARGB(bitmap: Bitmap): String {
        val pixelBuffer = ByteBuffer.allocate(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(pixelBuffer)
        val base64Pixels = Base64.Default.encode(pixelBuffer.array())
        return base64Pixels
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun base64ARGBToBitmap(base64Pixels: String, width: Int, height: Int): Bitmap {
        val pixels = Base64.Default.decode(base64Pixels.toByteArray())
        val pixelBuffer = ByteBuffer.allocate(pixels.count())
        pixelBuffer.put(pixels)
        pixelBuffer.flip()
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(pixelBuffer)
        return bitmap
    }
}