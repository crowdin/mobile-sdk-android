package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crowdin.platform.example.R

class SlideshowFragment : Fragment() {

    companion object {
        fun newInstance(): SlideshowFragment = SlideshowFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_slideshow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        val titles = resources.getStringArray(R.array.tab_array)
        for (index in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(index)!!.text = titles[index]
        }

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.dynamic_tab_title)))
    }
}