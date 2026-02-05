package com.crowdin.platform.util

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.annotation.MenuRes
import com.crowdin.platform.Crowdin
import com.crowdin.platform.data.model.SupportedLanguages
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

const val NEW_LINE = "<br>"
private const val DEFAULT_DATE_TIME_FORMAT = "yyyy_MM_dd-HH_mm_ss"
private val crowdinCodeMapping = mapOf("iw" to "he", "in" to "id")

fun MenuInflater.inflateWithCrowdin(
    @MenuRes menuRes: Int,
    menu: Menu,
    resources: Resources,
) {
    this.inflate(menuRes, menu)
    Crowdin.updateMenuItemsText(menuRes, menu, resources)
}

fun Long.parseToDateTimeFormat(): String {
    val monthDate = SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.getDefault())
    val cal = Calendar.getInstance(TimeZone.getDefault())
    cal.timeInMillis = this
    return monthDate.format(cal.time)
}

fun Locale.getFormattedCode(): String = "${language.withCrowdinSupportedCheck()}-$country"

fun executeIO(function: () -> Unit) {
    try {
        function.invoke()
    } catch (ex: IOException) {
        Log.w("Operation failed", ex)
    } catch (ex: RuntimeException) {
        Log.w("Operation failed", ex)
    }
}

fun Locale.toLanguageTagCompat(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        toLanguageTag()
    } else {
        val language = language.withCrowdinSupportedCheck()
        if (country.isNotEmpty()) "$language-$country" else language
    }

fun getMatchedCode(
    configuration: Configuration?,
    list: List<String>?,
    supportedLanguages: SupportedLanguages?,
): String? {
    val languageCode = configuration.getLocale().language.withCrowdinSupportedCheck()
    val code = "$languageCode-${Locale.getDefault().country}"

    if (supportedLanguages != null) {
        for (languageData in supportedLanguages) {
            if (languageData.value.locale == code) {
                return languageData.key
            }
        }
    }

    if (list?.contains(code) == false) {
        return languageCode.takeIf { list.contains(languageCode) }
    }
    return code
}

fun String.withCrowdinSupportedCheck(): String = crowdinCodeMapping[this] ?: this

fun String.unEscapeQuotes(): String =
    this
        .replace("\\\"", "\"")
        .replace("\\\'", "\'")
        .replace("\\n", NEW_LINE)

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

fun Configuration?.getLocale(): Locale {
    this ?: return Locale.getDefault()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        if (locales.isEmpty) {
            Locale.getDefault()
        } else {
            locales.get(0)
        }
    } else {
        @Suppress("DEPRECATION")
        locale ?: Locale.getDefault()
    }
}
