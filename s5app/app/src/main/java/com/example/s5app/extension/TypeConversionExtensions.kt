package com.example.s5app.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

// Function to convert Bitmap to Uri
fun Bitmap.toUri(context: Context): Uri {
    val cachePath = File(context.cacheDir, "my_images")
    cachePath.mkdirs()
    val file = File(cachePath, "image_${UUID.randomUUID()}.png")
    try {
        val stream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return FileProvider.getUriForFile(context, "com.example.s5app.provider", file)
}

// Function to convert Uri back to Bitmap
fun Uri.toBitmap(context: Context): Bitmap? {
    val inputStream: InputStream? = context.contentResolver.openInputStream(this)
    return BitmapFactory.decodeStream(inputStream)
}

fun getRawResourceUri(context: Context, resId: Int): Uri {
    return Uri.parse("android.resource://${context.packageName}/$resId")
}


