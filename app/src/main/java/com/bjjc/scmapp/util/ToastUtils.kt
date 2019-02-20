package com.bjjc.scmapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast


/**
 * Created by Allen on 2018/12/11 11:35
 */
object ToastUtils {
    private var toast: Toast? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    /**
     * Shows shortToast by string.
     * @param context 上下文
     * @param content 要显示的内容
     */
    @SuppressLint("ShowToast")
    fun showToastS(context: Context, content: String?) {
        mainHandler.post {
            //已在主线程中，可以更新UI
            if (toast == null) {
                toast = Toast.makeText(context, content, Toast.LENGTH_SHORT)
            } else {
                toast?.setText(content)
            }
            toast?.show()
        }
    }

    /**
     *Shows longToast by string.
     * @param context 上下文
     * @param content 要显示的内容
     */
    @SuppressLint("ShowToast")
    fun showToastL(context: Context, content: String) {
        mainHandler.post {
            if (toast == null) {
                toast = Toast.makeText(context, content, Toast.LENGTH_LONG)
            } else {
                toast?.setText(content)
            }
            toast?.show()
        }
    }

    /**
     * Shows shortToast by id of source
     * @param context 上下文
     * @param resId 要显示的资源id
     */
    fun showToastS(context: Context, resId: Int) {
        showToastS(context, context.resources.getText(resId) as String)
    }

    /**
     * Shows longToast by id of source
     * @param context 上下文
     * @param resId 要显示的资源id
     */
    fun showToastL(context: Context, resId: Int) {
        showToastL(context, context.resources.getText(resId) as String)
    }
}