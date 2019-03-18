package com.bjjc.scmapp.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import com.bjjc.scmapp.app.App

/**
 * Created by Allen on 2019/03/11 10:19
 */
object UIUtils {
    // Get Context.
    fun getContext(): Context {
        return App.getContext()
    }
    // Get Handler.
    fun getHandler(): Handler? {
        return App.getHandler()
    }
    // Get MainThreadId.
    fun getMainThreadId(): Int {
        return App.getMainThreadId()
    }
    // Get string from xml of strings.
    fun getString(id: Int): String? {
        return getContext().resources?.getString(id)
    }
    // Get string array from xml of strings.
    fun getStringArray(id: Int): Array<String>? {
        return getContext().resources?.getStringArray(id)
    }
    //Get drawable from file in resources of drawable.
    fun getDrawable(id: Int): Drawable? {
        return getContext().resources?.getDrawable(id)
    }
    // Get color from file in resources of color.
    fun getColor(id: Int): Int? {
        return getContext().resources?.getColor(id)
    }
    // Get dimension from xml of dimens.
    fun getDimen(id: Int): Int? {
        return getContext().resources?.getDimensionPixelSize(id)
    }
    // Transform dp to px.
    fun dp2px(dp: Float): Int? {
        val density: Float = getContext().resources?.displayMetrics?.density!!
        return density.let { dp * it + 0.5f }.toInt()
    }
    // Transform px to dp.
    fun px2dp(px: Int): Float {
        val density: Float = getContext().resources?.displayMetrics?.density!!
        return density.let { px / it }
    }
    // Load file of layout.
    fun inflate(id:Int):View{
        return View.inflate(getContext(),id,null)
    }
    //Determine if the current thread is running on the main thread.
    fun isRunOnUIThread():Boolean{
        //Id of the current thread fetched,if being the same as the main thread,so being a main thread.
        val myTid = android.os.Process.myTid()
        return getMainThreadId()== myTid
    }
    // Make the current thread is running on the main thread.
    fun runOnUIThread(r:Runnable){
        r.takeIf { isRunOnUIThread()}?.run()
     /* If the current thread is a main thread so that run directly,
        otherwise if being a child Thread and make it run on main thread through Handler.*/
        if (isRunOnUIThread()) r.run() else getHandler()?.post(r)
    }
}