package com.bjjc.scmapp.util

import android.app.ProgressDialog
import android.content.Context

/**
 * Created by Allen on 2018/12/10 14:03
 */
object ProgressDialogUtils {
    fun showProgressDialog(context:Context,title:String,message:String="请稍后..."):ProgressDialog{
        val pd = ProgressDialog(context)
        pd.setTitle(title)
        pd.setMessage(message)
        pd.setCancelable(true)
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pd.show()
        return pd
    }
}