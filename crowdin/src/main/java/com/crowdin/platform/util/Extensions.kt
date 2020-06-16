package com.crowdin.platform.util

import android.content.res.Resources
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.MenuRes
import com.crowdin.platform.Crowdin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private const val DEFAULT_DATE_TIME_FORMAT = "yyyy_MM_dd-HH_mm_ss"

fun MenuInflater.inflateWithCrowdin(@MenuRes menuRes: Int, menu: Menu, resources: Resources) {
    this.inflate(menuRes, menu)
    Crowdin.updateMenuItemsText(menuRes, menu, resources)
}

fun Long.parseToDateTimeFormat(): String {
    val monthDate = SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.getDefault())
    val cal = Calendar.getInstance(TimeZone.getDefault())
    cal.timeInMillis = this
    return monthDate.format(cal.time)
}

fun Locale.getFormattedCode(): String {
    val formattedLanguageCode = "$language-$country"

    return if (isSupported(formattedLanguageCode)) {
        formattedLanguageCode
    } else {
        language
    }
}

fun String.getLocaleForLanguageCode(): Locale {
    var code = Locale.getDefault().language
    return try {
        val localeData = this.split("-").toTypedArray()
        code = localeData[0]
        val region = localeData[1]
        Locale(code, region)
    } catch (ex: Exception) {
        Locale(code)
    }
}
