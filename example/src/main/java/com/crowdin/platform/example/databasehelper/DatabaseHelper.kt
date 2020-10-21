package com.crowdin.platform.example.databasehelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.crowdin.platform.example.utils.CATEGORY_NAME
import com.crowdin.platform.example.utils.DB_NAME
import com.crowdin.platform.example.utils.DB_VERSION
import com.crowdin.platform.example.utils.ID
import com.crowdin.platform.example.utils.TABLE_CATEGORY
import com.crowdin.platform.example.utils.TABLE_TASK
import com.crowdin.platform.example.utils.TASK_CATEGORY
import com.crowdin.platform.example.utils.TASK_DATE
import com.crowdin.platform.example.utils.TASK_FINISH
import com.crowdin.platform.example.utils.TASK_TASK
import com.crowdin.platform.example.utils.TASK_TIME
import com.crowdin.platform.example.utils.TASK_TITLE

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CATEGORY_TABLE)
        db.execSQL(CREATE_TASK_TABLE)

        val cv = ContentValues()
        cv.put(CATEGORY_NAME, "Personal")
        db.insert(TABLE_CATEGORY, null, cv)

        val cv1 = ContentValues()
        cv1.put(CATEGORY_NAME, "Business")
        db.insert(TABLE_CATEGORY, null, cv1)

        val cv2 = ContentValues()
        cv2.put(CATEGORY_NAME, "Insurance")
        db.insert(TABLE_CATEGORY, null, cv2)

        val cv3 = ContentValues()
        cv3.put(CATEGORY_NAME, "Shopping")
        db.insert(TABLE_CATEGORY, null, cv3)

        val cv4 = ContentValues()
        cv4.put(CATEGORY_NAME, "Banking")
        db.insert(TABLE_CATEGORY, null, cv4)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(DROP_CATEGORY_TABLE)
        db.execSQL(DROP_TASK_TABLE)
        onCreate(db)
    }

    companion object {

        /**************** Category ****************/

        private const val CREATE_CATEGORY_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CATEGORY_NAME + " TEXT); "

        private const val DROP_CATEGORY_TABLE = "DROP TABLE IF EXISTS $TABLE_CATEGORY"

        /****************** Task ******************/

        private const val CREATE_TASK_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TASK + "(" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TASK_TITLE + " TEXT, " +
                    TASK_TASK + " TEXT, " +
                    TASK_CATEGORY + " TEXT, " +
                    TASK_DATE + " TEXT, " +
                    TASK_TIME + " TEXT, " +
                    TASK_FINISH + " TEXT); "

        private const val DROP_TASK_TABLE = "DROP TABLE IF EXISTS $TABLE_TASK"
    }
}