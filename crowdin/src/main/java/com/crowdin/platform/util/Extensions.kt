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

// Android reports the obsolete ISO 639 codes for these languages, Crowdin uses the current ones.
private val crowdinCodeMapping = mapOf("iw" to "he", "in" to "id", "ji" to "yi")

private const val SCRIPT_HANS = "Hans"
private const val SCRIPT_HANT = "Hant"
private const val SCRIPT_LATIN = "Latn"

/*
 * CLDR parent-locale data (`supplementalData.xml` -> `parentLocales`). Android resolves bundled
 * resources through the same table, so the SDK mirrors it to land on the translation a user would
 * actually see: es-MX -> es-419 -> es instead of es-MX -> es.
 */
private val es419Regions =
    setOf(
        "AR",
        "BO",
        "BR",
        "BZ",
        "CL",
        "CO",
        "CR",
        "CU",
        "DO",
        "EC",
        "GT",
        "HN",
        "MX",
        "NI",
        "PA",
        "PE",
        "PR",
        "PY",
        "SV",
        "US",
        "UY",
        "VE",
    )

// CLDR treats `pt-BR`, not `pt-PT`, as the root Portuguese.
private val ptPtRegions = setOf("AO", "CH", "CV", "FR", "GQ", "GW", "LU", "MO", "MZ", "ST", "TL")

// Used to infer the script when the locale carries no script subtag.
private val chineseTraditionalRegions = setOf("HK", "MO", "TW")
private val chineseSimplifiedRegions = setOf("CN", "SG")

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
    val locale = configuration.getLocale()
    val languageCode = locale.language.withCrowdinSupportedCheck()
    val code = "$languageCode-${locale.country}"

    // `languages.json` and `manifest.json` are cached independently and can disagree, so a
    // language is only usable when the manifest actually ships content for it.
    if (supportedLanguages != null) {
        for ((crowdinCode, details) in supportedLanguages) {
            if (details.locale == code && list?.contains(crowdinCode) != false) {
                return crowdinCode
            }
        }
    }

    if (list == null || list.contains(code)) {
        return code
    }

    locale.parentLocaleCodes(languageCode).firstOrNull { list.contains(it) }?.let { return it }

    return languageCode.takeIf { list.contains(languageCode) }
}

/**
 * Crowdin language codes to try between an exact `language-REGION` match and the bare language
 * subtag, closest CLDR parent first. Empty when the language has no parent-locale group.
 */
internal fun Locale.parentLocaleCodes(languageCode: String): List<String> =
    parentLocaleCodes(languageCode, country.uppercase(Locale.ROOT), scriptCompat())

/** Serbian Latin is `sr-CS` in Crowdin, not the BCP 47 `sr-Latn`. */
internal fun parentLocaleCodes(
    languageCode: String,
    region: String,
    script: String,
): List<String> =
    when (languageCode) {
        "es" -> if (region in es419Regions) listOf("es-419") else emptyList()
        "pt" -> if (region in ptPtRegions) listOf("pt-PT") else emptyList()
        "zh" -> chineseParentCodes(region, script)
        "sr" -> if (script == SCRIPT_LATIN) listOf("sr-CS") else emptyList()
        else -> emptyList()
    }

/** CLDR chains zh-Hant-MO through zh-Hant-HK before reaching zh-Hant. */
private fun chineseParentCodes(
    region: String,
    script: String,
): List<String> {
    val traditional = script == SCRIPT_HANT || (script.isEmpty() && region in chineseTraditionalRegions)
    val simplified = script == SCRIPT_HANS || (script.isEmpty() && region in chineseSimplifiedRegions)

    return when {
        traditional && region == "MO" -> listOf("zh-HK", "zh-TW")
        traditional -> listOf("zh-TW")
        simplified -> listOf("zh-CN")
        else -> emptyList()
    }
}

// Locales cannot carry a script below API 21.
private fun Locale.scriptCompat(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) script else ""

/**
 * Manifest paths reach the distribution unencoded, and the CDN answers 403 for a literal `+`.
 * Android BCP 47 resource folders are the case that matters: `values-b+es+419`.
 */
fun String.encodeDistributionPath(): String = replace("+", "%2B")

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
