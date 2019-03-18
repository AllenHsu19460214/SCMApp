package com.bjjc.scmapp.util.dialog_custom.impl

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.bjjc.scmapp.util.dialog_custom.IDialogBuilder
import kotlinx.android.synthetic.main.layout_dialog_custom_yes_no.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.windowManager

/**
 * Created by Allen on 2019/03/15 13:35
 */
abstract class DialogBuilderImpl(private val mContext: Context) : IDialogBuilder {
    private val mBuilder = AlertDialog.Builder(mContext)
    protected var mLayoutId: Int = -1
    private lateinit var mDialog: Dialog
    override fun buildDialog(title: String, message: String, actionPositive: () -> Unit, actionNegative: () -> Unit) {
        setLayOutId()
        mDialog = mBuilder
            .setView(initDialogView(title, message, actionPositive, actionNegative))
            .setCancelable(false)
            .create()
        mDialog.run {
            show()
            window?.apply {
                val wm = context.windowManager
                val display = wm.defaultDisplay // Gets width and height of the screen.
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                // Gets and sets the current layout param of the mDialog box.
                attributes = attributes.apply {
                    // Set the height to 0.6 of the screen,according to the actual circumstance adjust。
                    height = (display.height * 0.5).toInt()
                    // Set the width to 0.7 of the screen,according to the actual circumstance adjust。
                    width = (display.width * 0.7).toInt()
                    alpha = 1F
                }
            }

        }
    }

    @SuppressLint("InflateParams")
    private fun initDialogView(
        title: String,
        message: String,
        actionPositive: () -> Unit = {},
        actionNegative: () -> Unit = {}
    ) =
        LayoutInflater.from(mContext).inflate(mLayoutId, null).apply {
            tvTitle.text = title
            tvMessage.text = message
            btnYes?.onClick {
                mDialog.dismiss()
                actionPositive()
            }
            btnNo?.onClick {
                mDialog.dismiss()
                actionNegative()
            }
        }

     abstract override fun setLayOutId()
}