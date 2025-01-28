package com.example.s5app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PDFUtil {
    fun savePDFFile(context: Context, byteArray: ByteArray, fileName: String): File? {
        val pdfFile = File(context.getExternalFilesDir(null), "$fileName.pdf")
        return try {
            val fos = FileOutputStream(pdfFile)
            fos.write(byteArray)
            fos.close()
            pdfFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun sharePdfFile(context: Context, pdfFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            pdfFile
        )

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share PDF file"))
    }
}