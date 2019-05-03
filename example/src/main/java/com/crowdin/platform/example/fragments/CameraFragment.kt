package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.R
import com.crowdin.platform.util.ScreenshotUtils

class CameraFragment : Fragment() {

    companion object {
        fun newInstance(): CameraFragment = CameraFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.textView3).setOnClickListener {
            ScreenshotUtils.getBitmapFromView(view, activity!!) { Crowdin.sendScreenshot(it) }
        }
    }
}