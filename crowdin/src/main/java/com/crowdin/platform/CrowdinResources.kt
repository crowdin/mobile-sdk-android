package com.crowdin.platform

import android.content.res.Resources
import android.icu.text.PluralRules
import android.os.Build
import android.text.Html
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import java.util.*

/**
 * This is the wrapped resources which will be provided by Crowdin.
 * For getting strings and texts, it checks the strings repository first and if there's a new string
 * that will be returned, otherwise it will fallback to the original resource strings.
 */
internal class CrowdinResources(
    res: Resources,
    private val dataManager: DataManager
) :
    Resources(
        res.assets,
        res.displayMetrics,
        res.configuration
    ) {

    @Throws(NotFoundException::class)
    override fun getString(id: Int): String {
        val entryName = getResourceEntryName(id)
        val string = getStringFromRepository(id) ?: super.getString(id)
        saveStringDataToCopy(entryName, string)

        return string
    }

    @Throws(NotFoundException::class)
    override fun getString(id: Int, vararg formatArgs: Any): String {
        val entryName = getResourceEntryName(id)
        val string = getStringFromRepository(id)
        val formattedString =
            if (string == null) {
                super.getString(id, *formatArgs)
            } else {
                String.format(string, *formatArgs)
            }

        saveStringDataToCopy(entryName, formattedString, formatArgs)

        return formattedString
    }

    @Throws(NotFoundException::class)
    override fun getStringArray(id: Int): Array<String> {
        val entryName = getResourceEntryName(id)
        val stringArray = getStringArrayFromRepository(id) ?: super.getStringArray(id)
        saveStringArrayDataToCopy(entryName, stringArray)

        return stringArray
    }

    @Throws(NotFoundException::class)
    override fun getText(id: Int): CharSequence {
        val entryName = getResourceEntryName(id)
        val string = getStringFromRepository(id)
        val formattedString = string?.fromHtml() ?: super.getText(id)
        saveStringDataToCopy(entryName, formattedString.toString())

        return formattedString
    }

    override fun getText(id: Int, default: CharSequence): CharSequence {
        val entryName = getResourceEntryName(id)
        val string = getStringFromRepository(id)
        val formattedString = string?.fromHtml() ?: super.getText(id, default)
        saveStringDataToCopy(entryName, formattedString.toString(), default = default)

        return formattedString
    }

    @Throws(NotFoundException::class)
    override fun getQuantityText(id: Int, quantity: Int): CharSequence {
        val plural = getPluralFromRepository(id, quantity)
        val formattedPlural = plural?.fromHtml() ?: super.getQuantityText(id, quantity)
        savePluralToCopy(id, quantity, formattedPlural.toString())

        return formattedPlural
    }

    @Throws(NotFoundException::class)
    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any?): String {
        val plural = getPluralFromRepository(id, quantity)
        val formattedPlural =
            if (plural == null) {
                super.getQuantityString(id, quantity, *formatArgs)
            } else {
                String.format(plural, *formatArgs)
            }

        savePluralToCopy(id, quantity, formattedPlural.toString(), formatArgs)

        return formattedPlural
    }

    private fun saveStringDataToCopy(
        entryName: String,
        string: String,
        formatArgs: Array<out Any?> = arrayOf(),
        default: CharSequence = ""
    ) {
        dataManager.saveReserveResources(
            StringData(
                entryName,
                string,
                formatArgs,
                StringBuilder(default)
            )
        )
    }

    private fun saveStringArrayDataToCopy(key: String, resultText: Array<String>) {
        dataManager.saveReserveResources(arrayData = ArrayData(key, resultText))
    }

    private fun savePluralToCopy(
        id: Int,
        quantity: Int,
        defaultText: String,
        formatArgs: Array<out Any?> = arrayOf()
    ) {
        val entryName = getResourceEntryName(id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val rule = PluralRules.forLocale(Locale.getDefault())
            val ruleName = rule.select(quantity.toDouble())
            val quantityMap = mutableMapOf<String, String>()
            quantityMap[ruleName] = defaultText
            val pluralData = PluralData(
                entryName,
                quantityMap,
                quantity,
                formatArgs
            )

            dataManager.saveReserveResources(pluralData = pluralData)
        }
    }

    private fun getStringFromRepository(id: Int): String? =
        try {
            val entryName = getResourceEntryName(id)
            dataManager.getString(Locale.getDefault().toString(), entryName)
        } catch (ex: NotFoundException) {
            null
        }

    private fun getStringArrayFromRepository(id: Int): Array<String>? {
        val entryName = getResourceEntryName(id)
        return dataManager.getStringArray(entryName)
    }

    private fun getPluralFromRepository(id: Int, quantity: Int): String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val entryName = getResourceEntryName(id)
            val rule = PluralRules.forLocale(Locale.getDefault())
            val ruleName = rule.select(quantity.toDouble())
            dataManager.getStringPlural(entryName, ruleName)
        } else {
            null
        }
}

fun String.fromHtml(): CharSequence =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Html.fromHtml(this)
    } else {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    }
