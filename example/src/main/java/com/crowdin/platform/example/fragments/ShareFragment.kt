package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.R

class ShareFragment : Fragment() {

    companion object {
        fun newInstance(): ShareFragment = ShareFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.textView0).setOnClickListener {
            Crowdin.sendScreenshot(view, activity!!)
        }

        view.findViewById<TextView>(R.id.textView0).text = resources.getQuantityString(R.plurals.test_plurals, 0, 0)
        view.findViewById<TextView>(R.id.textView1).text = resources.getQuantityString(R.plurals.test_plurals, 1, 1)
        view.findViewById<TextView>(R.id.textView2).text = resources.getQuantityString(R.plurals.test_plurals, 2, 2)
        view.findViewById<TextView>(R.id.textView3).text = resources.getQuantityString(R.plurals.test_plurals, 4, 4)

        view.findViewById<TextView>(R.id.textView4).text = resources.getQuantityText(R.plurals.test_plurals, 0)
        view.findViewById<TextView>(R.id.textView5).text = resources.getQuantityText(R.plurals.test_plurals, 1)
        view.findViewById<TextView>(R.id.textView6).text = resources.getQuantityText(R.plurals.test_plurals, 2)
        view.findViewById<TextView>(R.id.textView7).text = resources.getQuantityText(R.plurals.test_plurals, 5)
    }
}