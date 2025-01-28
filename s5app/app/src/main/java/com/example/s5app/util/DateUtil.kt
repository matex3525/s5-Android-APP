package com.example.s5app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    fun formatMillisecondsToDateTime(milliseconds: Long): String {
        val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        val date = Date(milliseconds)
        return sdf.format(date)
    }
}