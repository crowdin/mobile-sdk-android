package com.crowdin.platform

import android.annotation.SuppressLint
import android.content.res.AssetFileDescriptor
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.content.res.XmlResourceParser
import android.content.res.loader.ResourcesLoader
import android.graphics.Movie
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.icu.text.PluralRules
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.RequiresApi
import com.crowdin.platform.data.DataManager
import com.crowdin.platform.data.model.ArrayData
import com.crowdin.platform.data.model.PluralData
import com.crowdin.platform.data.model.StringData
import com.crowdin.platform.util.fromHtml
import com.crowdin.platform.util.getFormattedCode
import com.crowdin.platform.util.getLocale
import com.crowdin.platform.util.replaceNewLine
import java.io.InputStream

/**
 * This is the wrapped resources which will be provided by Crowdin.
 * For getting strings and texts, it checks the strings repository first and if there's a new string
 * that will be returned, otherwise it will fallback to the original resource strings.
 */
internal class CrowdinResources(
    private val res: Resources,
    private val dataManager: DataManager,
) : Resources(
        res.assets,
        res.displayMetrics,
        res.configuration,
    ) {
    @Throws(NotFoundException::class)
    override fun getString(id: Int): String {
        val entryName = getResourceEntryName(id)
        val string = getStringFromRepository(id)?.replaceNewLine() ?: res.getString(id)
        saveStringDataToCopy(entryName, string)

        return string
    }

    @Throws(NotFoundException::class)
    override fun getString(
        id: Int,
        vararg formatArgs: Any,
    ): String {
        val entryName = getResourceEntryName(id)
        val string = getStringFromRepository(id)?.replaceNewLine()
        val formattedString =
            if (string == null) {
                res.getString(id, *formatArgs)
            } else {
                try {
                    String.format(string, *formatArgs)
                } catch (ex: Exception) {
                    res.getString(id, *formatArgs)
                }
            }

        saveStringDataToCopy(entryName, formattedString, formatArgs)

        return formattedString
    }

    @Throws(NotFoundException::class)
    override fun getStringArray(id: Int): Array<String> {
        val entryName = getResourceEntryName(id)
        val stringArray = getStringArrayFromRepository(id) ?: res.getStringArray(id)
        saveStringArrayDataToCopy(entryName, stringArray)

        return stringArray
    }

    @Throws(NotFoundException::class)
    override fun getText(id: Int): CharSequence {
        val entryName = getResourceEntryName(id)
        val string = getStringFromRepository(id)
        val formattedString = string?.fromHtml() ?: res.getText(id)
        saveStringDataToCopy(entryName, formattedString.toString())

        return formattedString
    }

    override fun getText(
        id: Int,
        default: CharSequence,
    ): CharSequence {
        val entryName = getResourceEntryName(id)
        val string = getStringFromRepository(id)
        val formattedString = string?.fromHtml() ?: res.getText(id, default)
        saveStringDataToCopy(entryName, formattedString.toString(), default = default)

        return formattedString
    }

    @Throws(NotFoundException::class)
    override fun getQuantityText(
        id: Int,
        quantity: Int,
    ): CharSequence {
        val plural = getPluralFromRepository(id, quantity)
        val formattedPlural = plural?.fromHtml() ?: res.getQuantityText(id, quantity)
        savePluralToCopy(id, quantity, formattedPlural.toString())

        return formattedPlural
    }

    @Throws(NotFoundException::class)
    override fun getQuantityString(
        id: Int,
        quantity: Int,
        vararg formatArgs: Any?,
    ): String {
        val plural = getPluralFromRepository(id, quantity)
        val formattedPlural =
            if (plural == null) {
                res.getQuantityString(id, quantity, *formatArgs)
            } else {
                try {
                    String.format(plural, *formatArgs)
                } catch (ex: Exception) {
                    res.getQuantityString(id, quantity, *formatArgs)
                }
            }

        savePluralToCopy(id, quantity, formattedPlural, formatArgs)

        return formattedPlural
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun addLoaders(vararg loaders: ResourcesLoader?) {
        res.addLoaders(*loaders)
    }

    override fun getAnimation(id: Int): XmlResourceParser = res.getAnimation(id)

    override fun getConfiguration(): Configuration = res.configuration

    override fun getBoolean(id: Int): Boolean = res.getBoolean(id)

    override fun getDisplayMetrics(): DisplayMetrics = res.displayMetrics

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getColor(
        id: Int,
        theme: Theme?,
    ): Int = res.getColor(id, theme)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getColorStateList(
        id: Int,
        theme: Theme?,
    ): ColorStateList = res.getColorStateList(id, theme)

    override fun getDimension(id: Int): Float = res.getDimension(id)

    override fun getDimensionPixelOffset(id: Int): Int = res.getDimensionPixelOffset(id)

    override fun getDimensionPixelSize(id: Int): Int = res.getDimensionPixelSize(id)

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getDrawable(
        id: Int,
        theme: Theme?,
    ): Drawable = res.getDrawable(id, theme)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getDrawableForDensity(
        id: Int,
        density: Int,
        theme: Theme?,
    ): Drawable? = res.getDrawableForDensity(id, density, theme)

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun getFloat(id: Int): Float = res.getFloat(id)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getFont(id: Int): Typeface = res.getFont(id)

    override fun getFraction(
        id: Int,
        base: Int,
        pbase: Int,
    ): Float = res.getFraction(id, base, pbase)

    override fun getIntArray(id: Int): IntArray = res.getIntArray(id)

    override fun getInteger(id: Int): Int = res.getInteger(id)

    override fun getLayout(id: Int): XmlResourceParser = res.getLayout(id)

    @SuppressLint("DiscouragedApi")
    override fun getIdentifier(
        name: String?,
        defType: String?,
        defPackage: String?,
    ): Int = res.getIdentifier(name, defType, defPackage)

    override fun getResourceEntryName(resid: Int): String = res.getResourceEntryName(resid)

    override fun getResourceName(resid: Int): String = res.getResourceName(resid)

    override fun getResourcePackageName(resid: Int): String = res.getResourcePackageName(resid)

    override fun getResourceTypeName(resid: Int): String = res.getResourceTypeName(resid)

    override fun getTextArray(id: Int): Array<CharSequence> = res.getTextArray(id)

    override fun getQuantityString(
        id: Int,
        quantity: Int,
    ): String = res.getQuantityString(id, quantity)

    override fun toString(): String = res.toString()

    override fun getValue(
        id: Int,
        outValue: TypedValue?,
        resolveRefs: Boolean,
    ) {
        res.getValue(id, outValue, resolveRefs)
    }

    @SuppressLint("DiscouragedApi")
    override fun getValue(
        name: String?,
        outValue: TypedValue?,
        resolveRefs: Boolean,
    ) {
        res.getValue(name, outValue, resolveRefs)
    }

    override fun getXml(id: Int): XmlResourceParser = res.getXml(id)

    override fun getValueForDensity(
        id: Int,
        density: Int,
        outValue: TypedValue?,
        resolveRefs: Boolean,
    ) {
        res.getValueForDensity(id, density, outValue, resolveRefs)
    }

    override fun obtainTypedArray(id: Int): TypedArray = res.obtainTypedArray(id)

    override fun openRawResource(id: Int): InputStream = res.openRawResource(id)

    override fun obtainAttributes(
        set: AttributeSet?,
        attrs: IntArray?,
    ): TypedArray = res.obtainAttributes(set, attrs)

    override fun openRawResourceFd(id: Int): AssetFileDescriptor = res.openRawResourceFd(id)

    override fun openRawResource(
        id: Int,
        value: TypedValue?,
    ): InputStream = res.openRawResource(id, value)

    override fun parseBundleExtra(
        tagName: String?,
        attrs: AttributeSet?,
        outBundle: Bundle?,
    ) {
        res.parseBundleExtra(tagName, attrs, outBundle)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun removeLoaders(vararg loaders: ResourcesLoader?) {
        res.removeLoaders(*loaders)
    }

    override fun parseBundleExtras(
        parser: XmlResourceParser?,
        outBundle: Bundle?,
    ) {
        res.parseBundleExtras(parser, outBundle)
    }

    @Deprecated("Deprecated in Java")
    override fun getColor(id: Int): Int = res.getColor(id)

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Deprecated("Deprecated in Java")
    override fun getColorStateList(id: Int): ColorStateList = res.getColorStateList(id)

    @SuppressLint("UseCompatLoadingForDrawables")
    @Deprecated("Deprecated in Java")
    override fun getDrawable(id: Int): Drawable = res.getDrawable(id)

    @Deprecated("Deprecated in Java")
    override fun getMovie(id: Int): Movie = res.getMovie(id)

    @Deprecated("Deprecated in Java")
    override fun getDrawableForDensity(
        id: Int,
        density: Int,
    ): Drawable? = res.getDrawableForDensity(id, density)

    @Deprecated("Deprecated in Java")
    override fun updateConfiguration(
        config: Configuration?,
        metrics: DisplayMetrics?,
    ) {
        res.updateConfiguration(config, metrics)
    }

    private fun saveStringDataToCopy(
        entryName: String,
        string: String,
        formatArgs: Array<out Any?> = arrayOf(),
        default: CharSequence = "",
    ) {
        dataManager.saveReserveResources(
            locale = configuration.getLocale(),
            stringData =
                StringData(
                    entryName,
                    string,
                    formatArgs,
                    StringBuilder(default),
                ),
        )
    }

    private fun saveStringArrayDataToCopy(
        key: String,
        resultText: Array<String>,
    ) {
        dataManager.saveReserveResources(locale = configuration.getLocale(), arrayData = ArrayData(key, resultText))
    }

    private fun savePluralToCopy(
        id: Int,
        quantity: Int,
        defaultText: String,
        formatArgs: Array<out Any?> = arrayOf(),
    ) {
        val entryName = getResourceEntryName(id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val rule = PluralRules.forLocale(configuration.getLocale())
            val ruleName = rule.select(quantity.toDouble())
            val quantityMap = mutableMapOf<String, String>()
            quantityMap[ruleName] = defaultText
            val pluralData =
                PluralData(
                    entryName,
                    quantityMap,
                    quantity,
                    formatArgs,
                )

            dataManager.saveReserveResources(locale = configuration.getLocale(), pluralData = pluralData)
        }
    }

    private fun getStringFromRepository(id: Int): String? =
        try {
            val entryName = getResourceEntryName(id)
            dataManager.getString(configuration.getLocale().getFormattedCode(), entryName)
        } catch (ex: NotFoundException) {
            null
        }

    private fun getStringArrayFromRepository(id: Int): Array<String>? {
        val entryName = getResourceEntryName(id)
        val localeCode = configuration.getLocale().getFormattedCode()

        return dataManager.getStringArray(localeCode, entryName)
    }

    private fun getPluralFromRepository(
        id: Int,
        quantity: Int,
    ): String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val locale = configuration.getLocale()
            val localeCode = locale.getFormattedCode()
            val entryName = getResourceEntryName(id)
            val rule = PluralRules.forLocale(locale)
            val ruleName = rule.select(quantity.toDouble())
            dataManager.getStringPlural(localeCode, entryName, ruleName)
        } else {
            null
        }
}
