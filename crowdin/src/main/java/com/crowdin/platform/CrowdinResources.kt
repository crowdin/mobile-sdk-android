package com.crowdin.platform

import android.content.res.Resources
import android.icu.text.PluralRules
import android.os.Build
import android.text.Html
import com.crowdin.platform.repository.StringDataManager
import com.crowdin.platform.repository.model.ArrayData
import com.crowdin.platform.repository.model.PluralData
import com.crowdin.platform.repository.model.StringData
import java.util.*

/**
 * This is the wrapped resources which will be provided by Crowdin.
 * For getting strings and texts, it checks the strings repository first and if there's a new string
 * that will be returned, otherwise it will fallback to the original resource strings.
 */
internal class CrowdinResources(res: Resources, private val stringDataManager: StringDataManager) :
        Resources(res.assets, res.displayMetrics, res.configuration) {

    // TODO: refactor, extract methods. Remove duplication.

    @Throws(NotFoundException::class)
    override fun getString(id: Int): String {
        val value = getStringFromRepository(id)
        return if (value == null) {
            val key = getResourceEntryName(id)
            val defValue = super.getString(id)
            stringDataManager.saveReserveResources(StringData(key, defValue.toString()))
            defValue
        } else {
            value
        }
    }

    @Throws(NotFoundException::class)
    override fun getString(id: Int, vararg formatArgs: Any): String {
        val value = getStringFromRepository(id)
        return if (value == null) {
            val key = getResourceEntryName(id)
            val defValue = super.getString(id, *formatArgs)
            stringDataManager.saveReserveResources(StringData(key, defValue.toString(), formatArgs))
            defValue
        } else {
            String.format(value, *formatArgs)
        }
    }

    @Throws(NotFoundException::class)
    override fun getStringArray(id: Int): Array<String> {
        val value = getStringArrayFromRepository(id)
        return if (value == null) {
            val key = getResourceEntryName(id)
            val defArray = super.getStringArray(id)
            stringDataManager.saveReserveResources(arrayData = ArrayData(key, defArray))
            defArray
        } else {
            value
        }
    }

    @Throws(NotFoundException::class)
    override fun getText(id: Int): CharSequence {
        val value = getStringFromRepository(id)
        return if (value == null) {
            val key = getResourceEntryName(id)
            val defValue = super.getText(id)
            stringDataManager.saveReserveResources(StringData(key, defValue.toString()))
            defValue
        } else {
            fromHtml(value)
        }
    }

    override fun getText(id: Int, default: CharSequence): CharSequence {
        val value = getStringFromRepository(id)
        return if (value == null) {
            val key = getResourceEntryName(id)
            val defValue = super.getText(id, default)
            val stringData = StringData(key, defValue.toString(), def = StringBuilder(default))
            stringDataManager.saveReserveResources(stringData)
            defValue
        } else {
            fromHtml(value)
        }
    }

    // TODO: save with value as key. Potential issue when getQuantityText invoked but not set into view
    @Throws(NotFoundException::class)
    override fun getQuantityText(id: Int, quantity: Int): CharSequence {
        val value = getPluralFromRepository(id, quantity)
        return if (value == null) {
            val pluralKey = getResourceEntryName(id)
            val defaultText = super.getQuantityText(id, quantity)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val rule = PluralRules.forLocale(Locale.getDefault())
                val ruleName = rule.select(quantity.toDouble())

                val quantityMap = mutableMapOf<String, String>()
                quantityMap[ruleName] = defaultText.toString()
                val pluralData = PluralData(
                        pluralKey,
                        quantityMap,
                        quantity)

                stringDataManager.saveReserveResources(pluralData = pluralData)
            }
            defaultText
        } else {
            fromHtml(value)
        }
    }

    @Throws(NotFoundException::class)
    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any?): String {
        val value = getPluralFromRepository(id, quantity)
        return if (value == null) {
            val pluralKey = getResourceEntryName(id)
            val defaultText = super.getQuantityString(id, quantity, *formatArgs)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val rule = PluralRules.forLocale(Locale.getDefault())
                val ruleName = rule.select(quantity.toDouble())

                val quantityMap = mutableMapOf<String, String>()
                quantityMap[ruleName] = defaultText.toString()
                val pluralData = PluralData(
                        pluralKey,
                        quantityMap,
                        quantity,
                        formatArgs)

                stringDataManager.saveReserveResources(pluralData = pluralData)
            }
            defaultText
        } else {
            String.format(value, *formatArgs)
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

    private fun fromHtml(source: String): CharSequence =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Html.fromHtml(source)
            } else {
                Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT)
            }
}
