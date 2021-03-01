package com.crowdin.platform.example.task

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.crowdin.platform.example.databasehelper.DatabaseHelper
import com.crowdin.platform.example.task.model.TaskModel
import com.crowdin.platform.example.utils.ID
import com.crowdin.platform.example.utils.TABLE_TASK
import com.crowdin.platform.example.utils.TASK_CATEGORY
import com.crowdin.platform.example.utils.TASK_DATE
import com.crowdin.platform.example.utils.TASK_FINISH
import com.crowdin.platform.example.utils.TASK_IS_FINISH
import com.crowdin.platform.example.utils.TASK_IS_NOT_FINISH
import com.crowdin.platform.example.utils.TASK_TASK
import com.crowdin.platform.example.utils.TASK_TIME
import com.crowdin.platform.example.utils.TASK_TITLE

class DBManagerTask(private val context: Context) {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase

    private fun open(): DBManagerTask {
        dbHelper = DatabaseHelper(context)
        database = dbHelper.writableDatabase
        return this
    }

    private fun close() {
        dbHelper.close()
    }

    /**
     * Insert value in task table
     */
    fun insert(
        title: String,
        task: String,
        category: String,
        date: String = "",
        time: String = ""
    ) {
        open()
        val contentValues = ContentValues().apply {
            put(TASK_TITLE, title)
            put(TASK_TASK, task)
            put(TASK_CATEGORY, category)
            put(TASK_DATE, date)
            put(TASK_TIME, time)
            put(TASK_FINISH, TASK_IS_NOT_FINISH)
        }
        database.insert(TABLE_TASK, null, contentValues)
        close()
    }

    /**
     * Delete row in task table
     */
    fun delete(id: Int) {
        open()
        database.delete(TABLE_TASK, "$ID=$id", null)
        close()
    }

    /**
     * Get task list from Task table
     */
    fun getTaskList(): ArrayList<TaskModel> {
        open()
        val arrayList = ArrayList<TaskModel>()
        val query = "SELECT * FROM $TABLE_TASK"
        val cursor = database.rawQuery(query, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    val isFinish =
                        Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(TASK_FINISH)))
                    if (isFinish == TASK_IS_NOT_FINISH) {
                        val taskModel = TaskModel(
                            Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ID))),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_TASK)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_TIME))
                        )
                        arrayList.add(taskModel)
                    }
                } catch (ex: IllegalArgumentException) {
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return arrayList
    }

    /**
     * Finish task
     * */
    fun finishTask(id: Int) {
        open()
        val contentValues = ContentValues()
        contentValues.put(TASK_FINISH, TASK_IS_FINISH)
        database.update(TABLE_TASK, contentValues, "$ID = $id", null)
        close()
    }

    fun getHistoryTaskList(): java.util.ArrayList<TaskModel> {
        open()
        val arrayList = ArrayList<TaskModel>()
        val query = "SELECT * FROM $TABLE_TASK"
        val cursor = database.rawQuery(query, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    val isFinish =
                        Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(TASK_FINISH)))
                    if (isFinish == TASK_IS_FINISH) {
                        val taskModel = TaskModel(
                            Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ID))),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_TASK)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TASK_TIME))
                        )
                        arrayList.add(taskModel)
                    }
                } catch (ex: IllegalArgumentException) {
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return arrayList
    }

    fun unFinishTask(id: Int) {
        open()
        val contentValues = ContentValues()
        contentValues.put(TASK_FINISH, TASK_IS_NOT_FINISH)
        database.update(TABLE_TASK, contentValues, "$ID = $id", null)
        close()
    }
}
