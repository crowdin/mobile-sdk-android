package com.crowdin.crowdin_controls

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.crowdin.platform.Crowdin
import com.crowdin.platform.Crowdin.CROWDIN_TAG
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.data.remote.TranslationDownloadCallback
import com.crowdin.platform.screenshot.ScreenshotCallback
import java.lang.ref.WeakReference

class CrowdinWidgetService : Service(), LoadingStateListener {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var collapsedView: View
    private lateinit var expandedView: View
    private lateinit var authBtn: ToggleButton
    private lateinit var captureScreenshotBtn: Button
    private lateinit var realTimeBtn: ToggleButton
    private lateinit var screenshotEt: EditText

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("InflateParams")
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
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, // Allow focusable with input
            PixelFormat.TRANSLUCENT
        )

        // Specify the view position
        // Initially view will be added to top-left corner
        params.gravity = Gravity.CENTER or Gravity.START

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
        authBtn.setOnClickListener { updateAuthState() }

        realTimeBtn = floatingView.findViewById(R.id.realTimeBtn)
        realTimeBtn.setOnClickListener { updateRealTimeConnection() }

        captureScreenshotBtn = floatingView.findViewById(R.id.screenshotBtn)

        screenshotEt = floatingView.findViewById(R.id.screenshotEt)
        screenshotEt.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        screenshotEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(screenshotEt)
                true
            } else {
                false
            }
        }

        floatingView.findViewById<Button>(R.id.screenshotBtn)
            .setOnClickListener { captureScreenshot() }

        floatingView.findViewById<Button>(R.id.forceReloadBtn)
            .setOnClickListener { reloadData() }

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

    override fun onDataChanged() {
        showToast(DISTRIBUTION_RELOADED)
        Crowdin.unregisterDataLoadingObserver(this)
    }

    override fun onFailure(throwable: Throwable) {
        showToast(throwable.message ?: RELOAD_FAILED)
        Crowdin.unregisterDataLoadingObserver(this)
    }

    private fun updateAuthState() {
        collapseView()

        if (Crowdin.isAuthorized()) {
            Crowdin.logOut()
        } else {
            Crowdin.authorize(this) { error -> showToast(error) }
        }
    }

    private fun updateRealTimeConnection() {
        if (Crowdin.isRealTimeUpdatesConnected()) {
            Crowdin.disconnectRealTimeUpdates()
        } else {
            if (Crowdin.isAuthorized()) {
                Crowdin.createRealTimeConnection()
            } else {
                showToast(AUTH_REQUIRED)
                realTimeBtn.isChecked = false
            }
        }
    }

    private fun captureScreenshot() {
        if (Crowdin.isAuthorized()) {
            showToast("Screenshot uploading in progress")
            sendBroadcast(Intent().apply {
                action = BROADCAST_SCREENSHOT
                putExtra(SCREENSHOT_NAME_KEY, screenshotEt.text.toString())
            })
        } else {
            showToast(AUTH_REQUIRED)
        }
    }

    private fun reloadData() {
        val logMsg = "Data reload in progress"

        showToast(logMsg)
        Log.v(CROWDIN_TAG, logMsg)

        if (Crowdin.isRealTimeUpdatesEnabled()) {
            Crowdin.downloadTranslation(object : TranslationDownloadCallback {
                override fun onSuccess() {
                    showToast(TRANSLATION_RELOADED)
                    Log.v(CROWDIN_TAG, TRANSLATION_RELOADED)
                }

                override fun onFailure(throwable: Throwable) {
                    showToast(throwable.message ?: RELOAD_FAILED)
                    Log.e(CROWDIN_TAG, throwable.message, throwable)
                }
            })
        } else {
            Crowdin.registerDataLoadingObserver(this)
            Crowdin.forceUpdate(this)
        }
    }

    private fun expandView() {
        updateState()
        collapsedView.visibility = View.GONE
        expandedView.visibility = View.VISIBLE
    }

    private fun updateState() {
        authBtn.isChecked = Crowdin.isAuthorized()

        realTimeBtn.isEnabled = Crowdin.isRealTimeUpdatesEnabled()
        realTimeBtn.isChecked = Crowdin.isRealTimeUpdatesConnected()

        val isEnabled = Crowdin.isCaptureScreenshotEnabled()
        captureScreenshotBtn.isEnabled = isEnabled
        screenshotEt.isVisible = isEnabled
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

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {

        private const val AUTH_REQUIRED =
            "Crowdin Authorization required for this action. Press 'Log in' button to authorize"
        private const val DISTRIBUTION_RELOADED =
            "Translations successfully reloaded from the distribution"
        private const val TRANSLATION_RELOADED = "The latest translations successfully reloaded"
        private const val RELOAD_FAILED = "Data reload failed"
        private const val SCREENSHOT_NAME_KEY = "screenshot_name_key"

        private const val BROADCAST_SCREENSHOT = "com.crowdin.crowdin_controls.broadcast.SCREENSHOT"
        private lateinit var receiver: BroadcastReceiver

        fun launchService(activity: WeakReference<Activity>) {
            activity.get()?.startService(Intent(activity.get(), CrowdinWidgetService::class.java))

            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == BROADCAST_SCREENSHOT) {
                        activity.get()?.let {
                            Crowdin.sendScreenshot(
                                activity = it,
                                screenshotName = intent.getStringExtra(SCREENSHOT_NAME_KEY),
                                screenshotCallback = object : ScreenshotCallback {
                                    override fun onSuccess() {
                                        it.showToast("Screenshot uploaded")
                                    }

                                    override fun onFailure(throwable: Throwable) {
                                        it.showToast("Screenshot upload failed")
                                    }
                                })
                        }
                    }
                }
            }

            activity.get()?.let {
                ContextCompat.registerReceiver(
                    it,
                    receiver,
                    IntentFilter().apply {
                        addAction(BROADCAST_SCREENSHOT)
                    },
                    ContextCompat.RECEIVER_NOT_EXPORTED,
                )
            }
        }

        fun destroyService(activity: Activity) {
            activity.stopService(Intent(activity, CrowdinWidgetService::class.java))

            if (this::receiver.isInitialized) {
                activity.unregisterReceiver(receiver)
            }
        }
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
