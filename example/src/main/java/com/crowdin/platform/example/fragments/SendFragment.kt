package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.R
import com.crowdin.platform.utils.ScreenshotUtils

class SendFragment : Fragment() {

    companion object {
        fun newInstance(): SendFragment = SendFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textView0 = view.findViewById<TextView>(R.id.textView0)
        val textView1 = view.findViewById<TextView>(R.id.textView1)
        val textView2 = view.findViewById<TextView>(R.id.textView2)
        val textView3 = view.findViewById<TextView>(R.id.textView3)

        textView0.text = getString(R.string.formatting_test0, 3)
        textView1.text = getString(R.string.formatting_test1, "Test")
        textView2.text = getString(R.string.formatting_test2, 3, "Test")

        val text = getText(R.string.formatting_test3)
        textView3.text = text

        view.findViewById<TextView>(R.id.textView0).setOnClickListener {
            ScreenshotUtils.getBitmapFromView(view, activity!!) { Crowdin.sendScreenshot(it) }
        }
    }
}