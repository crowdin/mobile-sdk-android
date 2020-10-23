package com.crowdin.platform.example.task

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.crowdin.platform.example.BaseActivity
import com.crowdin.platform.example.R
import com.crowdin.platform.example.category.CategoryAdd
import com.crowdin.platform.example.category.DBManagerCategory
import com.crowdin.platform.example.utils.dialogAddCategory
import com.crowdin.platform.example.utils.toastMessage
import kotlinx.android.synthetic.main.activity_add_task.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : BaseActivity(), View.OnClickListener, CategoryAdd,
    OnItemSelectedListener.SpinnerItemListener {

    private lateinit var calendar: Calendar
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
    private var date = ""
    private var time = ""
    private var title = ""
    private var task = ""
    private var categoryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        setSupportActionBar(toolbarAddTask)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        edtSetDate.setOnClickListener(this)
        edtSetTime.setOnClickListener(this)
        imgCancelDate.setOnClickListener(this)
        imgCancelTime.setOnClickListener(this)
        imgAddCategory.setOnClickListener(this)

        loadDataInSpinner()
    }

    override fun onSupportNavigateUp(): Boolean {
        checkTask()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_task, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_done -> {
                addTask()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        checkTask()
    }

    private fun checkTask() {
        title = edtTitle.text.toString().trim()
        task = edtTask.text.toString().trim()

        if (title != "" && task != "") {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.save_task))
                setMessage(getString(R.string.do_you_want_to_save_this_task))
                setPositiveButton(R.string.save) { _, _ -> addTask() }
                setNegativeButton(R.string.cancel) { _, _ -> finish() }
                create()
                show()
            }

        } else {
            finish()
        }
    }

    private fun addTask() {
        title = edtTitle.text.toString().trim()
        task = edtTask.text.toString().trim()

        when {
            title.isEmpty() -> toastMessage(this, getString(R.string.please_add_title))
            task.isEmpty() -> toastMessage(this, getString(R.string.please_add_task))
            else -> insertAndFinish(title, task, categoryName, date, time)
        }
    }

    private fun insertAndFinish(
        title: String,
        task: String,
        categoryName: String,
        date: String,
        time: String
    ) {
        val dbManager = DBManagerTask(this)
        dbManager.insert(
            title,
            task,
            categoryName,
            date,
            time
        )
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun loadDataInSpinner() {
        val dbManager = DBManagerCategory(this)
        var labels = dbManager.getListOfCategory()

        if (labels.isEmpty()) {
            val arrayList: ArrayList<String> = ArrayList()
            arrayList.add(getString(R.string.no_category_to_added))
            labels = arrayList.sorted()
        }

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = dataAdapter
        spinnerCategory.onItemSelectedListener = OnItemSelectedListener(this)
    }

    override fun onSpinnerItemSelected(item: String) {
        if (item != getString(R.string.no_category_to_added)) {
            if (item != "") {
                categoryName = item
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            edtSetDate -> {
                dateAndTime()
                setDate()
            }
            edtSetTime -> {
                dateAndTime()
                setTime()
            }
            imgCancelDate -> {
                edtSetDate.setText("")
                date = ""
                imgCancelDate.visibility = View.GONE
                if (relativeLayoutTime.visibility == View.VISIBLE) {
                    relativeLayoutTime.visibility = View.GONE
                    edtSetTime.setText("")
                    time = ""
                    imgCancelTime.visibility = View.GONE
                }

            }
            imgCancelTime -> {
                edtSetTime.setText("")
                time = ""
                imgCancelTime.visibility = View.GONE
            }
            imgAddCategory -> dialogAddCategory(this, this)
        }
    }

    private fun dateAndTime() {
        calendar = Calendar.getInstance()
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabelDate()
        }

        timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateLabelTime()
        }
    }

    private fun setDate() {
        DatePickerDialog(
            this, dateSetListener, calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
            show()
        }
    }

    private fun setTime() {
        TimePickerDialog(
            this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), false
        ).show()
    }

    private fun updateLabelTime() {
        val myFormat = "HH:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        time = sdf.format(calendar.time)
        val myFormat2 = "h:mm a"
        val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
        edtSetTime.setText(sdf2.format(calendar.time))
        imgCancelTime.visibility = View.VISIBLE
    }

    private fun updateLabelDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        date = sdf.format(calendar.time)
        val myFormat2 = "EEE, d MMM yyyy"
        val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
        edtSetDate.setText(sdf2.format(calendar.time))
        relativeLayoutTime.visibility = View.VISIBLE
        imgCancelDate.visibility = View.VISIBLE
    }

    override fun onCategoryAdded() {
        loadDataInSpinner()
    }
}
