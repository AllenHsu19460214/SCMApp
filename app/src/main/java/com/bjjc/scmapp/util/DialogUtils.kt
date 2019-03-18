package com.bjjc.scmapp.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.bjjc.scmapp.R
import org.jetbrains.anko.find
import org.jetbrains.anko.windowManager

/**
 * Created by Allen on 2018/12/24 9:44
 */
class DialogUtils private constructor() {
    private var context:Context?=null
    private var title: String? = null
    private var message: String? = null
    private var dialog: AlertDialog? = null
    private var layoutId:Int=-1
    private var onPositiveClickListener: OnPositiveClickListener? = null
    private var onNegativeClickListener: OnNegativeClickListener? = null
    companion object {
        fun instance(): DialogUtils {
            return DialogUtils()
        }
    }
    private fun builder(context: Context, layoutId: Int) {
        dialog = AlertDialog.Builder(context)
            .setView(dialogInitView(context, layoutId))
            .setCancelable(false)
            .create()
        dialog?.show()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 将对话框的大小按屏幕大小的百分比设置
        val dialogWindow = dialog?.window
        val m = context.windowManager
        val d = m.defaultDisplay // 获取屏幕宽、高度
        val p = dialogWindow?.attributes // 获取对话框当前的参数值
        p?.height = (d.height * 0.5).toInt()// 高度设置为屏幕的0.6，根据实际情况调整
        p?.width = (d.width * 0.7).toInt()// 宽度设置为屏幕的0.7，根据实际情况调整
        p?.alpha = 1F
        dialogWindow?.attributes = p
    }

    private fun dialogInitView(context: Context, layoutId: Int): View? {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater?.inflate(layoutId, null)
        dialogView?.find<TextView>(R.id.tvTitle)?.text = title
        dialogView?.find<TextView>(R.id.tvMessage)?.text = message
        dialogView?.find<Button>(R.id.btnYes)?.setOnClickListener {
            dialog?.dismiss()
            onPositiveClickListener?.onPositiveBtnClicked()
        }
        if (R.layout.layout_dialog_custom_yes_no == layoutId) {
            dialogView?.find<Button>(R.id.btnNo)?.setOnClickListener {
                dialog?.dismiss()
                onNegativeClickListener?.onNegativeBtnClicked()
            }
        }
        return dialogView
    }

    fun setOnPositiveClickListener(onPositiveClickListener: OnPositiveClickListener): DialogUtils {
        this.onPositiveClickListener = onPositiveClickListener
        return this
    }

    fun setOnNegativeClickListener(onNegativeClickListener: OnNegativeClickListener): DialogUtils {
        this.onNegativeClickListener = onNegativeClickListener
        return this
    }

    fun setTitle(title: String): DialogUtils {
        this.title = title
        return this
    }

    fun setMessage(message: String): DialogUtils {
        this.message = message

        return this
    }

    fun customDialogYesOrNo(
        context: Context
    ): DialogUtils {
        this.context=context
        this.layoutId=R.layout.layout_dialog_custom_yes_no
        return this
    }

    fun customDialogYes(
        context: Context
    ): DialogUtils {
        this.context=context
        this.layoutId=R.layout.layout_dialog_custom_yes
        return this
    }
    fun show(){
        builder(this.context!!, layoutId)
    }
    fun isCreated():Boolean{
        return dialog!=null
    }
    interface OnPositiveClickListener {
        fun onPositiveBtnClicked()
    }

    interface OnNegativeClickListener {
        fun onNegativeBtnClicked()
    }
}