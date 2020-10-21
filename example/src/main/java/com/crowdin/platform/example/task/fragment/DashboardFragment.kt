package com.crowdin.platform.example.task.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowdin.platform.example.R
import com.crowdin.platform.example.task.AddTaskActivity
import com.crowdin.platform.example.task.TaskAdapter
import com.crowdin.platform.example.task.DBManagerTask
import com.crowdin.platform.example.task.model.TaskModel
import com.crowdin.platform.example.utils.DASHBOARD_RECYCLEVIEW_REFRESH
import com.crowdin.platform.example.utils.getFormatDate
import com.crowdin.platform.example.utils.getFormatTime
import com.crowdin.platform.example.utils.views.RecyclerItemClickListener
import com.crowdin.platform.example.utils.views.OnStartDragListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlin.math.abs
import kotlin.math.roundToInt

class DashboardFragment : Fragment(), View.OnClickListener, OnStartDragListener {

    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var txtNoTask: TextView
    private lateinit var recyclerViewTask: RecyclerView

    private var mArrayList: ArrayList<TaskModel> = ArrayList()
    private lateinit var dbManager: DBManagerTask
    lateinit var taskAdapter: TaskAdapter

    private lateinit var mItemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        initialize(view)

        return view
    }

    private fun initialize(view: View) {
        fabAddTask = view.fabAddTask
        txtNoTask = view.txtNoTask
        recyclerViewTask = view.recyclerViewTask

        recyclerViewTask.setHasFixedSize(true)
        recyclerViewTask.layoutManager = LinearLayoutManager(activity)

        fabAddTask.setOnClickListener(this)

        dbManager = DBManagerTask(requireActivity())
        mArrayList = dbManager.getTaskList()

        taskAdapter = TaskAdapter(requireActivity(), mArrayList)
        recyclerViewTask.adapter = taskAdapter

        initSwipe()

        recyclerViewTask.addOnItemTouchListener(
            RecyclerItemClickListener(
                requireContext(),
                recyclerViewTask,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val holder: TaskAdapter.ViewHolder = TaskAdapter.ViewHolder(view)
                        clickForDetails(holder, position)
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                    }
                })
        )
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper.startDrag(viewHolder)
    }

    private fun clickForDetails(holder: TaskAdapter.ViewHolder, position: Int) {
        val taskList = taskAdapter.getList()

        if (holder.textTitle.visibility == View.GONE && holder.textTask.visibility == View.GONE) {
            holder.textTitle.visibility = View.VISIBLE
            holder.textTask.visibility = View.VISIBLE
            holder.txtShowTitle.maxLines = Integer.MAX_VALUE
            holder.txtShowTask.maxLines = Integer.MAX_VALUE

            if (taskList[position].date != "") {
                holder.txtShowDate.text = getFormatDate(taskList[position].date!!)
                holder.textDate.visibility = View.VISIBLE
                holder.txtShowDate.visibility = View.VISIBLE
            }

            if (taskList[position].time != "") {
                holder.txtShowTime.text = getFormatTime(taskList[position].time!!)
                holder.textTime.visibility = View.VISIBLE
                holder.txtShowTime.visibility = View.VISIBLE
            }

        } else {
            holder.textTitle.visibility = View.GONE
            holder.textTask.visibility = View.GONE
            holder.txtShowTask.maxLines = 1
            holder.txtShowTitle.maxLines = 1

            if (taskList[position].date != "") {
                holder.textDate.visibility = View.GONE
                holder.txtShowDate.visibility = View.GONE
            }

            if (taskList[position].time != "") {
                holder.textTime.visibility = View.GONE
                holder.txtShowTime.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isTaskListEmpty()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.fabAddTask -> {
                startActivityForResult(
                    Intent(activity, AddTaskActivity::class.java),
                    DASHBOARD_RECYCLEVIEW_REFRESH
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                DASHBOARD_RECYCLEVIEW_REFRESH -> {
                    mArrayList = dbManager.getTaskList()
                    taskAdapter.clearAdapter()
                    taskAdapter.setList(mArrayList)
                }
            }
        }
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    taskAdapter.deleteTask(position)
                    isTaskListEmpty()
                } else {
                    taskAdapter.finishTask(position)
                    isTaskListEmpty()
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val iconBitmap: Bitmap

                    if (dX > 0) {
                        iconBitmap =
                            BitmapFactory.decodeResource(resources, R.drawable.ic_check_white_png)

                        paint.color = ContextCompat.getColor(requireContext(), R.color.green)

                        canvas.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(), paint
                        )

                        // Set the image icon for Right side swipe
                        canvas.drawBitmap(
                            iconBitmap,
                            itemView.left.toFloat() + convertDpToPx(16),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - iconBitmap.height.toFloat()) / 2,
                            paint
                        )
                    } else {
                        iconBitmap =
                            BitmapFactory.decodeResource(resources, R.drawable.ic_delete_white_png)
                        paint.color = ContextCompat.getColor(requireContext(), R.color.red)
                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                        )

                        //Set the image icon for Left side swipe
                        canvas.drawBitmap(
                            iconBitmap,
                            itemView.right.toFloat() - convertDpToPx(16) - iconBitmap.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - iconBitmap.height.toFloat()) / 2,
                            paint
                        )
                    }

                    // Fade out the view as it is swiped out of the parent's bounds
                    val alpha: Float = 1.0f - abs(dX) / viewHolder.itemView.width.toFloat()
                    viewHolder.itemView.alpha = alpha
                    viewHolder.itemView.translationX = dX

                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewTask)
    }

    private fun convertDpToPx(dp: Int): Int {
        return (dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun isTaskListEmpty() {
        if (taskAdapter.itemCount == 0) {
            txtNoTask.visibility = View.VISIBLE
        } else {
            txtNoTask.visibility = View.GONE
        }
    }

}
