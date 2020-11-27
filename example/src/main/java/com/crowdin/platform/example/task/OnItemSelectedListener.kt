package com.crowdin.platform.example.task

import android.view.View
import android.widget.AdapterView

class OnItemSelectedListener(
    private val spinnerItemListener: SpinnerItemListener
) : AdapterView.OnItemSelectedListener {

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val item = parent!!.getItemAtPosition(pos).toString()
        spinnerItemListener.onSpinnerItemSelected(item)
    }

    interface SpinnerItemListener {

        fun onSpinnerItemSelected(item: String)
    }
}
