package com.bjjc.scmapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.view.ViewTreeObserver


/**
 * Created by Allen on 2019/01/25 11:22
 * 监控软键盘工具类
 */

class KeyBoardShowListener(private val ctx: Context) {
    var keyboardListener: OnKeyboardVisibilityListener? = null
        internal set

    interface OnKeyboardVisibilityListener {
        fun onVisibilityChanged(visible: Boolean)
    }

    fun setKeyboardListener(listener: OnKeyboardVisibilityListener, activity: Activity) {
        val activityRootView = (activity.findViewById(android.R.id.content) as ViewGroup).getChildAt(0)

        activityRootView.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                private var wasOpened: Boolean = false

                private val DefaultKeyboardDP = 100

                // From @nathanielwolf answer... Lollipop includes button bar in the root. Add height of button bar (48dp) to maxDiff
                @SuppressLint("ObsoleteSdkInt")
                private val EstimatedKeyboardDP =
                    DefaultKeyboardDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0

                private val r = Rect()

                override fun onGlobalLayout() { // Convert the dp to pixels.
                    val estimatedKeyboardHeight = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        EstimatedKeyboardDP.toFloat(),
                        activityRootView.resources.displayMetrics
                    ).toInt()

                    // Conclude whether the keyboard is shown or not.
                    activityRootView.getWindowVisibleDisplayFrame(r)
                    val heightDiff = activityRootView.rootView.height - (r.bottom - r.top)
                    val isShown = heightDiff >= estimatedKeyboardHeight

                    if (isShown == wasOpened) {
                        Log.e("Keyboard state", "Ignoring global layout change...")
                        return
                    }
                    wasOpened = isShown
                    listener.onVisibilityChanged(isShown)
                }
            })
    }
}
