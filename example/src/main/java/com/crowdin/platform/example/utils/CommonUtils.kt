package com.crowdin.platform.example.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Convert formatted Date
 */
fun getFormatDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

    return try {
        inputFormat.parse(inputDate)?.let { outputFormat.format(it) } ?: ""
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

/**
 * Convert formatted Time
 */
fun getFormatTime(inputTime: String): String {
    val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // HH:mm:ss
    val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    return try {
        inputFormat.parse(inputTime)?.let { outputFormat.format(it) } ?: ""
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}
