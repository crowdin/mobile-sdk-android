package com.crowdin.platform.example.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.crowdin.platform.example.R

internal class SampleAdapter(private val dataArray: Array<String>) : RecyclerView.Adapter<SampleAdapter.SampleViewHolder>() {

    internal inner class SampleViewHolder(internal val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleAdapter.SampleViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.item_row,
                parent, false) as TextView
        return SampleViewHolder(textView)
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.textView.text = dataArray[position]
    }

    override fun getItemCount(): Int {
        return dataArray.size
    }
}
