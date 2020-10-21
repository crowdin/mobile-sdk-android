package com.crowdin.platform.example.category.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.crowdin.platform.example.category.model.CategoryModel
import com.crowdin.platform.example.databasehelper.DatabaseHelper
import com.crowdin.platform.example.utils.CATEGORY_NAME
import com.crowdin.platform.example.utils.ID
import com.crowdin.platform.example.utils.TABLE_CATEGORY

class DBManagerCategory(val context: Context) {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase

    private fun open(): DBManagerCategory {
        dbHelper = DatabaseHelper(context)
        database = dbHelper.writableDatabase
        return this
    }

    private fun close() {
        dbHelper.close()
    }

    /**
     * Insert value in Category table
     */
    fun insert(category: String) {
        open()
        val contentValues = ContentValues()
        contentValues.put(CATEGORY_NAME, category)
        database.insert(TABLE_CATEGORY, null, contentValues)
        close()
    }

    /**
     * Update value in Category table
     */
    fun update(id: Int, categoryName: String) {
        open()
        val contentValue = ContentValues()
        contentValue.put(CATEGORY_NAME, categoryName)
        database.update(TABLE_CATEGORY, contentValue, "$ID = $id", null)
        close()
    }

    /**
     * Delete row in Category table
     */
    fun delete(id: Int) {
        open()
        database.delete(TABLE_CATEGORY, "$ID=$id", null)
        close()
    }

    /**
     * Get name from Category table
     */
    fun getCategoryName(id: Int): String {
        var categoryName = ""
        open()

        val query = "SELECT * FROM $TABLE_CATEGORY WHERE $ID=$id"
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                categoryName = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return categoryName
    }

    /**
     * Get category list from Category table
     */
    fun getCategoryList(): ArrayList<CategoryModel> {
        val arrayList = ArrayList<CategoryModel>()
        open()
        val query = "SELECT * FROM $TABLE_CATEGORY"
        val cursor = database.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
                val name = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME))
                val categoryModel = CategoryModel(id, name)
                arrayList.add(categoryModel)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return arrayList
    }

    /**
     * Get list of category names from Category table
     */
    fun getListOfCategory(): List<String> {
        open()
        val labels: ArrayList<String> = ArrayList()
        val query = "SELECT * FROM $TABLE_CATEGORY"
        val cursor = database.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)))

            } while (cursor.moveToNext())
        }
        cursor.close()
        close()

        return labels
    }
}
