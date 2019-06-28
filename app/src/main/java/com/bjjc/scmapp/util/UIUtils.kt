package com.bjjc.scmapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View

/**
 * Created by Allen on 2019/03/11 10:19
 */
@SuppressLint("StaticFieldLeak")
object UIUtils {
    lateinit var context:Context
    lateinit var handler:Handler
    var mainThreadId: Int = -1
    fun context(context: Context):UIUtils{
        this.context = context
        handler = Handler(Looper.getMainLooper())
        this.mainThreadId = android.os.Process.myTid()
        return this
    }

    // Get string from xml of strings.
    fun getString(id: Int): String? {
        return context.resources?.getString(id)
    }

    // Get string array from xml of strings.
    fun getStringArray(id: Int): Array<String>? {
        return context.resources?.getStringArray(id)
    }

    //Get drawable from file in resources of drawable.
    fun getDrawable(id: Int): Drawable? {
        return context.resources?.getDrawable(id)
    }

    // Get color from file in resources of color.
    fun getColor(id: Int): Int? {
        return context.resources?.getColor(id)
    }

    // Get dimension from xml of dimens.
    fun getDimen(id: Int): Int? {
        return context.resources?.getDimensionPixelSize(id)
    }

    // Transform dp to px.
    fun dp2px(dp: Float): Int {
        val density: Float = context.resources?.displayMetrics?.density!!
        return density.let { dp * it + 0.5f }.toInt()
    }

    // Transform px to dp.
    fun px2dp(px: Int): Float {
        val density: Float = context.resources?.displayMetrics?.density!!
        return density.let { px / it }
    }

    // Load file of layout.
    fun inflate(id: Int): View {
        return View.inflate(context, id, null)
    }

    //Determine if the current thread is running on the main thread.
    fun isRunOnUIThread(): Boolean {
        //Id of the current thread fetched,if being the same as the main thread,so being a main thread.
        return mainThreadId == android.os.Process.myTid()
    }

    // Make the current thread is running on the main thread.
    fun runOnUIThread(r: Runnable) {
        r.takeIf { isRunOnUIThread() }?.run()
        /* If the current thread is a main thread so that run directly,
           otherwise if being a child Thread and make it run on main thread through Handler.*/
        if (isRunOnUIThread()) r.run() else handler.post(r)
    }


}