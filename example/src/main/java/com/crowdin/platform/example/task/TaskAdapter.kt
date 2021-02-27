package com.crowdin.platform.example.task

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.crowdin.platform.example.R
import com.crowdin.platform.example.task.model.TaskModel
import com.crowdin.platform.example.utils.getFormatDate
import com.crowdin.platform.example.utils.getFormatTime
import com.crowdin.platform.example.utils.views.ItemTouchHelperAdapter
import java.util.ArrayList
import java.util.Collections
import java.util.Random

class TaskAdapter(
    private val context: Context,
    private var list: ArrayList<TaskModel>
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    private val dbManager: DBManagerTask = DBManagerTask(context)

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mView = LayoutInflater.from(context).inflate(R.layout.row_task, parent, false)
        return ViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun clearAdapter() {
        this.list.clear()
        notifyDataSetChanged()
    }

    fun setList(mArrayList: ArrayList<TaskModel>) {
        this.list = mArrayList
        notifyDataSetChanged()
    }

    fun deleteTask(position: Int) {
        dbManager.delete(list[position].id)
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

    fun finishTask(position: Int) {
        dbManager.finishTask(list[position].id)
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

    fun unFinishTask(position: Int) {
        dbManager.unFinishTask(list[position].id)
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

    override fun onItemDismiss(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(list, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun getItemViewType(position: Int): Int = position

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val viewColor = view.findViewById<View>(R.id.viewColor)
        private val titleValueTv = view.findViewById<TextView>(R.id.titleValueTv)
        private val taskTextValueTv = view.findViewById<TextView>(R.id.taskTextValueTv)
        private val categoryTv = view.findViewById<TextView>(R.id.categoryTv)
        private val dateValueTv = view.findViewById<TextView>(R.id.dateValueTv)
        private val dateTitleTv = view.findViewById<TextView>(R.id.dateTitleTv)
        private val timeValueTv = view.findViewById<TextView>(R.id.timeValueTv)
        private val timeTitleTv = view.findViewById<TextView>(R.id.timeTitleTv)
        private val titleTv = view.findViewById<TextView>(R.id.titleTv)
        private val taskTitleTv = view.findViewById<TextView>(R.id.taskTitleTv)
        private val cardView = view.findViewById<CardView>(R.id.cardView)

        fun onBind(taskModel: TaskModel) {
            val androidColors = context.resources.getIntArray(R.array.random_color)
            val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
            viewColor.setBackgroundColor(randomAndroidColor)

            titleValueTv.text = taskModel.title
            taskTextValueTv.text = taskModel.task
            categoryTv.text = taskModel.category
            cardView.setOnClickListener { clickForDetails(taskModel) }
        }

        private fun clickForDetails(taskModel: TaskModel) {
            if (titleTv.visibility == View.GONE && taskTitleTv.visibility == View.GONE) {
                expandContent(taskModel)
            } else {
                collapseContent(taskModel)
            }
        }

        private fun expandContent(taskModel: TaskModel) {
            titleTv.visibility = View.VISIBLE
            taskTitleTv.visibility = View.VISIBLE
            titleValueTv.maxLines = Integer.MAX_VALUE
            taskTextValueTv.maxLines = Integer.MAX_VALUE

            if (taskModel.date.isNotEmpty()) {
                dateValueTv.text = getFormatDate(taskModel.date)
                dateTitleTv.visibility = View.VISIBLE
                dateValueTv.visibility = View.VISIBLE
            }

            if (taskModel.time.isNotEmpty()) {
                timeValueTv.text = getFormatTime(taskModel.time)
                timeTitleTv.visibility = View.VISIBLE
                timeValueTv.visibility = View.VISIBLE
            }
        }

        private fun collapseContent(taskModel: TaskModel) {
            titleTv.visibility = View.GONE
            taskTitleTv.visibility = View.GONE
            taskTextValueTv.maxLines = 1
            titleValueTv.maxLines = 1

            if (taskModel.date.isNotEmpty()) {
                dateTitleTv.visibility = View.GONE
                dateValueTv.visibility = View.GONE
            }

            if (taskModel.time.isNotEmpty()) {
                timeTitleTv.visibility = View.GONE
                timeValueTv.visibility = View.GONE
            }
        }
    }
}
