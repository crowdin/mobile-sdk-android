package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowdin.platform.Crowdin
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.example.R
import com.crowdin.platform.example.adapter.SampleAdapter

class GalleryFragment : Fragment(), LoadingStateListener {

    private lateinit var adapter: SampleAdapter
    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance(): GalleryFragment = GalleryFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val array = resources.getStringArray(R.array.string_array)
        adapter = SampleAdapter(array)
        recyclerView.adapter = adapter

        view.findViewById<TextView>(R.id.textView3).setOnClickListener { Crowdin.sendScreenshot(activity!!) }
        Crowdin.registerDataLoadingObserver(this)
    }

    override fun onDataChanged() {
        val array = resources.getStringArray(R.array.string_array)
        adapter.updateData(array)
        Log.d("Crowdin", "GalleryFragment: onSuccess")
    }

    override fun onFailure(throwable: Throwable) {
        Log.d("Crowdin", "GalleryFragment: onFailure ${throwable.localizedMessage}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Crowdin.unregisterDataLoadingObserver(this)
    }
}