package com.example.crowdin_controls

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import android.widget.ToggleButton
import com.crowdin.platform.Crowdin

class CrowdinWidgetService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private lateinit var authBtn: ToggleButton
    private lateinit var realTimeBtn: ToggleButton

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)
        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // Add the view to the window.
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Specify the view position
        // Initially view will be added to top-left corner
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 100

        //Add the view to the window
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

        collapsedView = floatingView.findViewById(R.id.collapse_view)
        expandedView = floatingView.findViewById(R.id.expandedContainer)

        floatingView.findViewById<ImageView>(R.id.collapsedCloseBtn)
            .setOnClickListener { stopSelf() }

        // Set the close button
        floatingView.findViewById<ImageView>(R.id.expandedCloseBtn).setOnClickListener {
            collapseView()
        }

        authBtn = floatingView.findViewById(R.id.authBtn)
        authBtn.setOnClickListener {
            collapseView()

            if (Crowdin.isAuthorized()) {
                Crowdin.logOut()
            } else {
                Crowdin.authorize(this)
            }
        }

        realTimeBtn = floatingView.findViewById(R.id.realTimeBtn)
        realTimeBtn.setOnClickListener {
            if (Crowdin.isRealTimeUpdatesEnabled()) {
                Crowdin.disconnectRealTimeUpdates()
            } else {
                if (Crowdin.isAuthorized()) {
                    Crowdin.createRealTimeConnection()
                } else {
                    Toast.makeText(this, "Authorization required", Toast.LENGTH_SHORT).show()
                    realTimeBtn.isChecked = false
                }
            }
        }

        // Drag and move floating view using user's touch action.
        floatingView.findViewById<View>(R.id.root_container)
            .setOnTouchListener(object : OnTouchListener {

                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // remember the initial position.
                            initialX = params.x
                            initialY = params.y

                            // get the touch location
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            val xDiff = (event.rawX - initialTouchX).toInt()
                            val yDiff = (event.rawY - initialTouchY).toInt()

                            // The check for xDiff <10 && yDiff< 10 because sometime elements moves a little while clicking.
                            // So that is click event.
                            if (xDiff < 10 && yDiff < 10) {
                                if (isViewCollapsed) {
                                    expandView()
                                }
                            }
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            // Calculate the X and Y coordinates of the view.
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()

                            // Update the layout with new X & Y coordinate
                            windowManager.updateViewLayout(floatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
    }

    private fun expandView() {
        updateState()
        collapsedView.visibility = View.GONE
        expandedView.visibility = View.VISIBLE
    }

    private fun updateState() {
        authBtn.isChecked = Crowdin.isAuthorized()
        realTimeBtn.isChecked = Crowdin.isRealTimeUpdatesEnabled()
    }

    private fun collapseView() {
        collapsedView.visibility = View.VISIBLE
        expandedView.visibility = View.GONE
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private val isViewCollapsed: Boolean
        get() = floatingView.findViewById<View>(R.id.collapse_view).visibility == View.VISIBLE

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingView)
    }

    companion object {

        fun launchService(activity: Activity) {
            activity.startService(Intent(activity, CrowdinWidgetService::class.java))
        }

        fun destroyService(activity: Activity) {
            activity.stopService(Intent(activity, CrowdinWidgetService::class.java))
        }
    }
}
