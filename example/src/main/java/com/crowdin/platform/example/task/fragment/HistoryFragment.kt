package com.crowdin.platform.example.task.fragment

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
import com.crowdin.platform.example.task.adapter.TaskAdapter
import com.crowdin.platform.example.task.database.DBManagerTask
import com.crowdin.platform.example.task.model.TaskModel
import com.crowdin.platform.example.utils.getFormatDate
import com.crowdin.platform.example.utils.getFormatTime
import com.crowdin.platform.example.utils.views.RecyclerItemClickListener
import com.crowdin.platform.example.utils.views.OnStartDragListener
import kotlinx.android.synthetic.main.fragment_history.view.*
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.roundToInt

class HistoryFragment : Fragment(), OnStartDragListener {

    private lateinit var txtNoHistory: TextView
    private lateinit var recyclerViewHistory: RecyclerView

    private var mArrayList: ArrayList<TaskModel> = ArrayList()
    private lateinit var dbManager: DBManagerTask
    lateinit var taskAdapter: TaskAdapter

    private lateinit var mItemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        initialize(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        isTaskListEmpty()
    }

    private fun initialize(view: View) {

        txtNoHistory = view.txtNoHistory
        recyclerViewHistory = view.recyclerViewHistory

        recyclerViewHistory.setHasFixedSize(true)
        recyclerViewHistory.layoutManager = LinearLayoutManager(requireActivity())

        dbManager = DBManagerTask(requireActivity())
        mArrayList = dbManager.getHistoryTaskList()

        taskAdapter = TaskAdapter(requireActivity(), mArrayList)
        recyclerViewHistory.adapter = taskAdapter

        initSwipe()

        recyclerViewHistory.addOnItemTouchListener(
            RecyclerItemClickListener(
                requireContext(),
                recyclerViewHistory,
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

    private fun initSwipe() {

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
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
                    taskAdapter.unFinishTask(position)
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

                if (actionState === ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    val itemView = viewHolder.itemView

                    val p = Paint()
                    val icon: Bitmap

                    if (dX > 0) {
                        /* Note, ApplicationManager is a helper class I created
                            myself to get a context outside an Activity class -
                            feel free to use your own method */

                        icon = BitmapFactory.decodeResource(
                            resources, R.drawable.ic_unfinish
                        )

                        /* Set your color for positive displacement */
                        p.color = ContextCompat.getColor(requireContext(), R.color.green)

                        // Draw Rect with varying right side, equal to displacement dX
                        canvas.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(), p
                        )

                        // Set the image icon for Right swipe
                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat() + convertDpToPx(16),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            p
                        )
                    } else {
                        icon = BitmapFactory.decodeResource(
                            resources, R.drawable.ic_delete_white_png
                        )

                        /* Set your color for negative displacement */
                        p.color = ContextCompat.getColor(requireContext(), R.color.red)

                        // Draw Rect with varying left side, equal to the item's right side
                        // plus negative displacement dX
                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), p
                        )

                        //Set the image icon for Left swipe
                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - convertDpToPx(16) - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            p
                        )
                    }

                    // Fade out the view as it is swiped out of the parent's bounds
                    val alpha = 1.0f - abs(dX) / viewHolder.itemView.width.toFloat()
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
        itemTouchHelper.attachToRecyclerView(recyclerViewHistory)
    }

    private fun convertDpToPx(dp: Int): Int {
        return (dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun isTaskListEmpty() {
        if (taskAdapter.itemCount == 0) {
            txtNoHistory.visibility = View.VISIBLE
        } else {
            txtNoHistory.visibility = View.GONE
        }
    }
}