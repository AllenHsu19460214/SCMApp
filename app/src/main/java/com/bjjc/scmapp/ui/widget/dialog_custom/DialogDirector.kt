package com.bjjc.scmapp.ui.widget.dialog_custom

/**
 * Created by Allen on 2019/03/15 11:59
 */
object DialogDirector{
    /**
     * @param dialogBuilder: Style of the dialog.
     * @param title: Title of the dialog.
     * @param message: Message of the dialog.
     * @param actionPositive: actionPositive of the dialog.
     * @param actionNegative: actionNegative of the dialog.
     */
    fun showDialog(dialogBuilder: IDialogBuilder, title:String, message:String, actionPositive:()->Unit={},actionNegative:()->Unit={}){
        dialogBuilder.buildDialog(title,message,actionPositive,actionNegative)
    }
}
