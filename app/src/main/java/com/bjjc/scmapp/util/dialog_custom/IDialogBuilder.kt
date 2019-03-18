package com.bjjc.scmapp.util.dialog_custom

/**
 * Created by Allen on 2019/03/15 10:28
 */
interface IDialogBuilder {
    fun buildDialog(title:String,message:String,actionPositive:()->Unit,actionNegative:()->Unit)
    fun setLayOutId()
}