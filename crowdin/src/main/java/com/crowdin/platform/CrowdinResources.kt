package com.crowdin.platform

import android.content.res.Resources
import android.icu.text.PluralRules
import android.os.Build
import android.text.Html
import com.crowdin.platform.data.StringDataManager
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import java.util.*

/**
 * This is the wrapped resources which will be provided by Crowdin.
 * For getting strings and texts, it checks the strings repository first and if there's a new string
 * that will be returned, otherwise it will fallback to the original resource strings.
 */
internal class CrowdinResources(res: Resources, private val stringDataManager: StringDataManager) :
        Resources(res.assets, res.displayMetrics, res.configuration) {

    @Throws(NotFoundException::class)
    override fun getString(id: Int): String {
        val key = getResourceEntryName(id)
        val resultText = getStringFromRepository(id) ?: super.getString(id)
        saveStringDataToCopy(key, resultText.toString())
        return resultText
    }

    @Throws(NotFoundException::class)
    override fun getString(id: Int, vararg formatArgs: Any): String {
        val key = getResourceEntryName(id)
        val value = getStringFromRepository(id)
        val resultText =
                if (value == null) {
                    super.getString(id, *formatArgs)
                } else {
                    String.format(value, *formatArgs)
                }
        saveStringDataToCopy(key, resultText.toString(), formatArgs)
        return resultText
    }

    @Throws(NotFoundException::class)
    override fun getStringArray(id: Int): Array<String> {
        val key = getResourceEntryName(id)
        val resultArray = getStringArrayFromRepository(id) ?: super.getStringArray(id)
        saveStringArrayDataToCopy(key, resultArray)
        return resultArray
    }

    @Throws(NotFoundException::class)
    override fun getText(id: Int): CharSequence {
        val key = getResourceEntryName(id)
        val value = getStringFromRepository(id)
        val resultText =
                if (value == null) {
                    super.getText(id)
                } else {
                    fromHtml(value)
                }
        saveStringDataToCopy(key, resultText.toString())
        return resultText
    }

    override fun getText(id: Int, default: CharSequence): CharSequence {
        val key = getResourceEntryName(id)
        val value = getStringFromRepository(id)
        val resultText =
                if (value == null) {
                    super.getText(id, default)
                } else {
                    fromHtml(value)
                }
        saveStringDataToCopy(key, resultText.toString(), default = default)
        return resultText
    }

    @Throws(NotFoundException::class)
    override fun getQuantityText(id: Int, quantity: Int): CharSequence {
        val value = getPluralFromRepository(id, quantity)
        val resultText =
                if (value == null) {
                    super.getQuantityText(id, quantity)
                } else {
                    fromHtml(value)
                }
        savePluralToCopy(id, quantity, resultText.toString())
        return resultText
    }

    @Throws(NotFoundException::class)
    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any?): String {
        val value = getPluralFromRepository(id, quantity)
        val resultText =
                if (value == null) {
                    super.getQuantityString(id, quantity, *formatArgs)
                } else {
                    String.format(value, *formatArgs)
                }
        savePluralToCopy(id, quantity, resultText.toString(), formatArgs)
        return resultText
    }

    private fun saveStringDataToCopy(key: String, resultText: String, formatArgs: Array<out Any?> = arrayOf(), default: CharSequence = "") {
        val stringData = StringData(key, resultText, formatArgs, StringBuilder(default))
        stringDataManager.saveReserveResources(stringData)
    }

    private fun saveStringArrayDataToCopy(key: String, resultText: Array<String>) {
        stringDataManager.saveReserveResources(arrayData = ArrayData(key, resultText))
    }

    private fun savePluralToCopy(id: Int, quantity: Int, defaultText: String, formatArgs: Array<out Any?> = arrayOf()) {
        val pluralKey = getResourceEntryName(id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val rule = PluralRules.forLocale(Locale.getDefault())
            val ruleName = rule.select(quantity.toDouble())

            val quantityMap = mutableMapOf<String, String>()
            quantityMap[ruleName] = defaultText
            val pluralData = PluralData(
                    pluralKey,
                    quantityMap,
                    quantity,
                    formatArgs)

            stringDataManager.saveReserveResources(pluralData = pluralData)
        }
    }

    private fun getStringFromRepository(id: Int): String? =
            try {
                val stringKey = getResourceEntryName(id)
                stringDataManager.getString(Locale.getDefault().toString(), stringKey)
            } catch (ex: NotFoundException) {
                null
            }

    private fun getStringArrayFromRepository(id: Int): Array<String>? {
        val key = getResourceEntryName(id)
        return stringDataManager.getStringArray(key)
    }

    private fun getPluralFromRepository(id: Int, quantity: Int): String? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val pluralKey = getResourceEntryName(id)
                val rule = PluralRules.forLocale(Locale.getDefault())
                val ruleName = rule.select(quantity.toDouble())
                stringDataManager.getStringPlural(pluralKey, ruleName)
            } else {
                null
            }
}

internal fun fromHtml(source: String): CharSequence =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Html.fromHtml(source)
        } else {
            Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT)
        }
