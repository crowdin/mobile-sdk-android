package com.crowdin.platform.example.task.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
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
import com.crowdin.platform.example.task.DBManagerTask
import com.crowdin.platform.example.task.TaskAdapter
import com.crowdin.platform.example.task.model.TaskModel
import com.crowdin.platform.example.utils.convertDpToPx
import com.crowdin.platform.example.utils.views.OnStartDragListener
import java.util.ArrayList
import kotlin.math.abs

class HistoryFragment : Fragment(), OnStartDragListener {

    private var list: ArrayList<TaskModel> = ArrayList()
    private lateinit var dbManager: DBManagerTask
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var txtNoHistory: TextView

    private lateinit var mItemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtNoHistory = view.findViewById(R.id.txtNoHistory)
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory)
        recyclerViewHistory.setHasFixedSize(true)
        recyclerViewHistory.layoutManager = LinearLayoutManager(requireActivity())

        dbManager = DBManagerTask(requireActivity())
        list = dbManager.getHistoryTaskList()

        taskAdapter = TaskAdapter(requireActivity(), list)
        recyclerViewHistory.adapter = taskAdapter

        initSwipe()
    }

    override fun onResume() {
        super.onResume()
        updateEmptyStateView()
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper.startDrag(viewHolder)
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
                    updateEmptyStateView()
                } else {
                    taskAdapter.unFinishTask(position)
                    updateEmptyStateView()
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
                    // Get RecyclerView item from the ViewHolder
                    val itemView = viewHolder.itemView
                    val p = Paint()
                    val icon: Bitmap

                    if (dX > 0) {
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_unfinish)
                        p.color = ContextCompat.getColor(requireContext(), R.color.green)
                        canvas.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(), p
                        )

                        // Set the image icon for Right swipe
                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat() + 16.convertDpToPx(resources),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            p
                        )
                    } else {
                        icon =
                            BitmapFactory.decodeResource(resources, R.drawable.ic_delete_white_png)
                        p.color = ContextCompat.getColor(requireContext(), R.color.red)
                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), p
                        )

                        //Set the image icon for Left swipe
                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - 16.convertDpToPx(resources) - icon.width,
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

    fun updateEmptyStateView() {
        if (taskAdapter.itemCount == 0) {
            txtNoHistory.visibility = View.VISIBLE
        } else {
            txtNoHistory.visibility = View.GONE
        }
    }
}
