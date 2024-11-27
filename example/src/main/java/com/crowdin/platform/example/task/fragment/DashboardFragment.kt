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
import kotlin.math.abs

class DashboardFragment : Fragment(), OnStartDragListener {
    private lateinit var dbManager: DBManagerTask
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: TextView
    private var taskList: ArrayList<TaskModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.dashboard)
        emptyStateView = view.findViewById(R.id.emptyStateView)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        dbManager = DBManagerTask(requireActivity())
        taskList = dbManager.getTaskList()

        taskAdapter = TaskAdapter(requireActivity(), taskList)
        recyclerView.adapter = taskAdapter

        if (taskList.isEmpty()) {
            addDefaultTask()
        }

        initSwipe()
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper.startDrag(viewHolder)
    }

    override fun onResume() {
        super.onResume()
        updateEmptyStateView()
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
                    updateEmptyStateView()
                } else {
                    taskAdapter.finishTask(position)
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
                            itemView.left.toFloat() + 16.convertDpToPx(resources),
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
                            itemView.right.toFloat() - 16.convertDpToPx(resources) - iconBitmap.width,
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
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun updateEmptyStateView() {
        if (taskAdapter.itemCount == 0) {
            emptyStateView.visibility = View.VISIBLE
        } else {
            emptyStateView.visibility = View.GONE
        }
    }

    private fun addDefaultTask() {
        val dbManager = DBManagerTask(requireContext())
        dbManager.insert(
            "Meet with Laura",
            "Design a prototype",
            "Business",
            "",
            ""
        )
        taskList = dbManager.getTaskList()
        taskAdapter.setList(taskList)
    }
}
