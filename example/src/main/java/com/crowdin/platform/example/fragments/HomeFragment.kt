package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.crowdin.platform.Crowdin
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.auth.CrowdinAuth
import com.crowdin.platform.example.R
import com.crowdin.platform.util.ScreenshotUtils

class HomeFragment : Fragment(), LoadingStateListener {

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.textView0).setOnClickListener {
            ScreenshotUtils.getBitmapFromView(view, activity!!) {
                view.findViewById<ImageView>(R.id.testImageView).setImageBitmap(it)
                Crowdin.sendScreenshot(it)
            }
        }

        view.findViewById<TextView>(R.id.textView1).setOnClickListener {
            context?.let { it1 -> CrowdinAuth.showDialog(it1) }
        }

        view.findViewById<Button>(R.id.load_data_btn).setOnClickListener {
            context?.let { Crowdin.forceUpdate(context!!) }
        }

        view.findViewById<TextView>(R.id.textView5).text = getString(R.string.text5, "str")
        view.findViewById<TextView>(R.id.textView6).text = getString(R.string.text6, "str", "str")
        view.findViewById<TextView>(R.id.textView7).text = getString(R.string.text7, "str", "str")

        Crowdin.registerDataLoadingObserver(this)
    }

    override fun onDataChanged() {
        Log.d("Crowdin", "HomeFragment: onSuccess")
    }

    override fun onFailure(throwable: Throwable) {
        Log.d("Crowdin", "HomeFragment: onFailure ${throwable.localizedMessage}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Crowdin.unregisterDataLoadingObserver(this)
    }
}