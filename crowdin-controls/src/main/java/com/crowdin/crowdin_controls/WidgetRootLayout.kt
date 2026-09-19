package com.crowdin.crowdin_controls

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.FrameLayout

/**
 * Root of the floating widget.
 *
 * While the panel is expanded its window takes input focus, so the screenshot name field can be
 * typed into - which also means the window, not the host activity, receives the Back key.
 * Handling it here lets Back close the panel instead of disappearing.
 */
class WidgetRootLayout
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : FrameLayout(context, attrs, defStyleAttr) {

        /** Invoked on Back. Return true when the event was handled. */
        var onBackPressed: (() -> Boolean)? = null

        override fun dispatchKeyEvent(event: KeyEvent): Boolean {
            if (event.keyCode == KeyEvent.KEYCODE_BACK &&
                event.action == KeyEvent.ACTION_UP &&
                onBackPressed?.invoke() == true
            ) {
                return true
            }
            return super.dispatchKeyEvent(event)
        }
    }
