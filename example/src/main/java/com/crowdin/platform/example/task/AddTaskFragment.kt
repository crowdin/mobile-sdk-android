package com.crowdin.platform.example.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.crowdin.platform.example.R
import com.crowdin.platform.example.category.CategoryAdd
import com.crowdin.platform.example.category.DBManagerCategory
import com.crowdin.platform.example.utils.dialogAddCategory
import com.crowdin.platform.example.utils.toastMessage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskFragment : Fragment(), View.OnClickListener, CategoryAdd,
    OnItemSelectedListener.SpinnerItemListener {

    private lateinit var edtTitle: EditText
    private lateinit var edtTask: EditText
    private lateinit var edtSetDate: EditText
    private lateinit var edtSetTime: EditText
    private lateinit var imgCancelDate: ImageView
    private lateinit var imgCancelTime: ImageView
    private lateinit var imgAddCategory: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var timeLayout: ConstraintLayout
    private lateinit var calendar: Calendar
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
    private var date = ""
    private var time = ""
    private var title = ""
    private var task = ""
    private var categoryName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.add_task)
        edtTitle = view.findViewById(R.id.edtTitle)
        edtTask = view.findViewById(R.id.edtTask)
        edtSetDate = view.findViewById(R.id.edtSetDate)
        edtSetTime = view.findViewById(R.id.edtSetTime)
        imgCancelDate = view.findViewById(R.id.imgCancelDate)
        imgCancelTime = view.findViewById(R.id.imgCancelTime)
        imgAddCategory = view.findViewById(R.id.imgAddCategory)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        timeLayout = view.findViewById(R.id.timeLayout)
        view.findViewById<Button>(R.id.btnSaveTask).setOnClickListener { addTask() }

        edtSetDate.setOnClickListener(this)
        edtSetTime.setOnClickListener(this)
        imgCancelDate.setOnClickListener(this)
        imgCancelTime.setOnClickListener(this)
        imgAddCategory.setOnClickListener(this)

        loadDataInSpinner()
    }

    private fun addTask() {
        title = edtTitle.text.toString().trim()
        task = edtTask.text.toString().trim()

        when {
            title.isEmpty() -> toastMessage(requireContext(), getString(R.string.please_add_title))
            task.isEmpty() -> toastMessage(requireContext(), getString(R.string.please_add_task))
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
        val dbManager = DBManagerTask(requireContext())
        dbManager.insert(
            title,
            task,
            categoryName,
            date,
            time
        )
        requireActivity().onBackPressed()
    }

    private fun loadDataInSpinner() {
        val dbManager = DBManagerCategory(requireContext())
        var labels = dbManager.getListOfCategory()

        if (labels.isEmpty()) {
            val arrayList: ArrayList<String> = ArrayList()
            arrayList.add(getString(R.string.no_category_to_added))
            labels = arrayList.sorted()
        }

        val dataAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, labels)
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
                if (timeLayout.visibility == View.VISIBLE) {
                    timeLayout.visibility = View.GONE
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

            imgAddCategory -> dialogAddCategory(requireContext(), this)
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
            requireContext(), dateSetListener, calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
            show()
        }
    }

    private fun setTime() {
        TimePickerDialog(
            requireContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
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
        timeLayout.visibility = View.VISIBLE
        imgCancelDate.visibility = View.VISIBLE
    }

    override fun onCategoryAdded() {
        loadDataInSpinner()
    }
}
