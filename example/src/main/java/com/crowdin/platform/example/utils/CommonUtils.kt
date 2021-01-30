package com.crowdin.platform.example.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.crowdin.platform.example.R
import com.crowdin.platform.example.category.CategoryAdd
import com.crowdin.platform.example.category.CategoryDelete
import com.crowdin.platform.example.category.CategoryUpdate
import com.crowdin.platform.example.category.DBManagerCategory
import com.crowdin.platform.util.getLocaleForLanguageCode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

fun toastMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

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

/**
 * Dialog to add category
 */
fun dialogAddCategory(context: Context, categoryAdd: CategoryAdd) {
    val li = LayoutInflater.from(context)
    val promptsView = li.inflate(R.layout.alert_dialog_with_input, null)
    val input = promptsView.findViewById(R.id.input_et) as EditText

    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.add_category))
        .setView(promptsView)
        .setPositiveButton(R.string.add) { _, _ ->
            val categoryName: String = input.text.toString().trim()
            if (categoryName != "") {
                val dbManager = DBManagerCategory(context)
                dbManager.insert(categoryName)
                categoryAdd.onCategoryAdded()

            } else {
                toastMessage(
                    context,
                    context.getString(R.string.please_enter_category_to_add)
                )
            }
        }
        .setNegativeButton(R.string.cancel) { _, _ -> }
        .show()
}

/**
 * Dialog to update category
 */
fun dialogUpdateCategory(context: Context, id: Int, categoryUpdate: CategoryUpdate) {
    val dbManager = DBManagerCategory(context)
    val catName = dbManager.getCategoryName(id)
    val li = LayoutInflater.from(context)
    val promptsView = li.inflate(R.layout.alert_dialog_with_input, null)
    val input = promptsView.findViewById(R.id.input_et) as EditText
    input.setText(catName)
    input.setSelection(input.text.length)

    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.update_category))
        .setView(promptsView)
        .setPositiveButton(R.string.update) { _, _ ->
            val categoryName = input.text.toString().trim()
            if (categoryName != "") {
                if (categoryName != catName) {
                    dbManager.update(id, categoryName)
                    val mArrayList = dbManager.getCategoryList()
                    categoryUpdate.isCategoryUpdated(mArrayList)
                } else {
                    toastMessage(
                        context,
                        context.getString(R.string.please_edit_category_to_update)
                    )
                }
            } else {
                toastMessage(context, context.getString(R.string.please_enter_something_to_update))
            }
        }
        .setNegativeButton(R.string.cancel) { _, _ -> }
        .show()
}

/**
 * Dialog to delete category
 */
fun dialogDeleteCategory(context: Context, id: Int, categoryDelete: CategoryDelete) {
    val dbManager = DBManagerCategory(context)
    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.dialog_delete_category_title))
        .setMessage(context.getString(R.string.dialog_delete_category_desc))
        .setPositiveButton(R.string.delete) { _, _ ->
            dbManager.delete(id)
            val mArrayList = dbManager.getCategoryList()
            categoryDelete.isCategoryDeleted(mArrayList)
        }
        .setNegativeButton(R.string.cancel) { _, _ -> }
        .show()
}

fun Int.convertDpToPx(resources: Resources): Int =
    (this * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()

fun Context.updateLocale(newLocaleCode: String) {
    val newLocale = newLocaleCode.getLocaleForLanguageCode()
    Locale.setDefault(newLocale)
    val configuration = this.resources.configuration
    configuration.setLocale(newLocale)
    this.createConfigurationContext(configuration)
}