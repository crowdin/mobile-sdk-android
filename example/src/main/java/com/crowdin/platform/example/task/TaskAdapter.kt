package com.crowdin.platform.example.task

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowdin.platform.example.R
import com.crowdin.platform.example.task.model.TaskModel
import com.crowdin.platform.example.utils.views.ItemTouchHelperAdapter
import kotlinx.android.synthetic.main.row_task.view.*
import java.util.ArrayList
import java.util.Collections
import java.util.Random

class TaskAdapter(
    private val mContext: Context,
    private var mArrayList: ArrayList<TaskModel>
) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    private val dbManager: DBManagerTask = DBManagerTask(mContext)

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mView = LayoutInflater.from(mContext).inflate(R.layout.row_task, parent, false)
        return ViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val androidColors = mContext.resources.getIntArray(R.array.random_color)
        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
        holder.viewColorTag.setBackgroundColor(randomAndroidColor)

        holder.txtShowTitle.text = mArrayList[position].title
        holder.txtShowTask.text = mArrayList[position].task
        holder.txtShowCategory.text = mArrayList[position].category
    }

    fun clearAdapter() {
        this.mArrayList.clear()
        notifyDataSetChanged()
    }

    fun setList(mArrayList: ArrayList<TaskModel>) {
        this.mArrayList = mArrayList
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<TaskModel> {
        return this.mArrayList
    }

    fun deleteTask(position: Int) {
        dbManager.delete(mArrayList[position].id!!)
        mArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mArrayList.size)
    }

    fun finishTask(position: Int) {
        dbManager.finishTask(mArrayList[position].id!!)
        mArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mArrayList.size)
    }

    fun unFinishTask(position: Int) {
        dbManager.unFinishTask(mArrayList[position].id!!)
        mArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mArrayList.size)
    }

    override fun onItemDismiss(position: Int) {
        mArrayList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(mArrayList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewColorTag = view.viewColorTag!!
        val txtShowTitle = view.txtShowTitle!!
        val txtShowTask = view.txtShowTask!!
        val txtShowCategory = view.txtShowCategory!!

        val txtShowDate = view.txtShowDate!!
        val textDate = view.textDate!!
        val txtShowTime = view.txtShowTime!!
        val textTime = view.textTime!!
        val textTitle = view.textTitle!!
        val textTask = view.textTask!!
    }
}