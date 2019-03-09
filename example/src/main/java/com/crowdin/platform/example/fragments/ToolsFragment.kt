package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.R

class ToolsFragment : Fragment() {

    companion object {
        fun newInstance(): ToolsFragment = ToolsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_tools, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> {}
            R.id.menu_crop -> {}
            R.id.menu_refresh -> {}
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_menu, menu)
        Crowdin.updateMenuItemsText(menu, resources, R.menu.fragment_menu)
    }
}