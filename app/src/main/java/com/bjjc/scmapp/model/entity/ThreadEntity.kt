package com.bjjc.scmapp.model.entity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler

/**
 * Created by Allen on 2019/06/14 9:54
 */
@SuppressLint("StaticFieldLeak")
object ThreadEntity {
    lateinit var context: Context
    var mainThreadId: Int = -1
    lateinit var handler: Handler
    fun context(context:Context){
        context.let {
            this.context = it
        }
    }
}