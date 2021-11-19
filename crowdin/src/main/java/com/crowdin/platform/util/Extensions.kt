package com.crowdin.platform.util

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.MenuRes
import com.crowdin.platform.Crowdin
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

const val NEW_LINE = "<br>"
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

fun Locale.getFormattedCode(): String = "$language-$country"

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

fun executeIO(function: () -> Unit) {
    try {
        function.invoke()
    } catch (ex: IOException) {
        Log.w("Operation failed", ex)
    } catch (ex: RuntimeException) {
        Log.w("Operation failed", ex)
    }
}

fun getMatchedCode(list: List<String>?): String? {
    val code = "${Locale.getDefault().language}-${Locale.getDefault().country}"
    if (list?.contains(code) == false) {
        val languageCode = Locale.getDefault().language
        return languageCode.takeIf { list.contains(languageCode) }
    }
    return code
}

fun String.unEscapeQuotes(): String {
    return this.replace("\\\"", "\"")
        .replace("\\\'", "\'")
        .replace("\\n", NEW_LINE)
}

fun String.fromHtml(): CharSequence? =
    try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Html.fromHtml(this)
        } else {
            Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
        }
    } catch (ex: Exception) {
        null
    }

fun String.replaceNewLine(): String = replace(NEW_LINE, NEW_LINE.fromHtml()?.toString() ?: "")
