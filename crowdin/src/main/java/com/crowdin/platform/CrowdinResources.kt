package com.crowdin.platform

import android.content.res.Resources
import android.icu.text.PluralRules
import android.os.Build
import android.text.Html

import com.crowdin.platform.repository.StringDataManager
import com.crowdin.platform.utils.LocaleUtils

/**
 * This is the wrapped resources which will be provided by Crowdin.
 * For getting strings and texts, it checks the strings repository first and if there's a new string
 * that will be returned, otherwise it will fallback to the original resource strings.
 */
internal class CrowdinResources(res: Resources, private val stringDataManager: StringDataManager) :
        Resources(res.assets, res.displayMetrics, res.configuration) {

    @Throws(Resources.NotFoundException::class)
    override fun getString(id: Int): String {
        val value = getStringFromRepository(id)
        return value ?: super.getString(id)
    }

    @Throws(Resources.NotFoundException::class)
    override fun getString(id: Int, vararg formatArgs: Any): String {
        val value = getStringFromRepository(id)
        return if (value == null) super.getString(id, *formatArgs) else String.format(value, *formatArgs)
    }

    @Throws(Resources.NotFoundException::class)
    override fun getStringArray(id: Int): Array<String> {
        val value = getStringArrayFromRepository(id)
        return value ?: super.getStringArray(id)
    }

    @Throws(Resources.NotFoundException::class)
    override fun getText(id: Int): CharSequence {
        val value = getStringFromRepository(id)
        return value?.let { fromHtml(it) } ?: super.getText(id)
    }

    override fun getText(id: Int, def: CharSequence): CharSequence {
        val value = getStringFromRepository(id)
        return value?.let { fromHtml(it) } ?: super.getText(id, def)
    }

    @Throws(Resources.NotFoundException::class)
    override fun getQuantityText(id: Int, quantity: Int): CharSequence {
        val value = getPluralFromRepository(id, quantity)
        return value?.let { fromHtml(it) } ?: super.getQuantityText(id, quantity)
    }

    private fun getStringFromRepository(id: Int): String? =
            try {
                val stringKey = getResourceEntryName(id)
                stringDataManager.getString(LocaleUtils.currentLanguage, stringKey)
            } catch (ex: Resources.NotFoundException) {
                null
            }

    private fun getStringArrayFromRepository(id: Int): Array<String>? {
        val key = getResourceEntryName(id)
        return stringDataManager.getStringArray(key)
    }

    private fun getPluralFromRepository(id: Int, quantity: Int): String? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val pluralKey = getResourceEntryName(id)
                val rule = PluralRules.forLocale(LocaleUtils.currentLocale)
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
