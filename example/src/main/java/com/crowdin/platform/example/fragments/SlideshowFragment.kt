package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.R
import com.google.android.material.tabs.TabLayout

class SlideshowFragment : Fragment() {

    companion object {
        fun newInstance(): SlideshowFragment = SlideshowFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_slideshow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        val titles = resources.getStringArray(R.array.tab_array)
        for (index in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(index)!!.text = titles[index]
        }

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.dynamic_tab_title)))

        view.findViewById<TextView>(R.id.textView3).setOnClickListener {
            // Screenshot functionality. Captures displayed views and sends it to Crowdin platform.
            Crowdin.sendScreenshot(activity!!)
        }
    }
}
