package com.bjjc.scmapp.ui.widget.dialog_custom

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import com.bjjc.scmapp.R

/**
 * Created by Allen on 2019/06/18 8:19
 */
class BottomUpDialog:Dialog {
    constructor(context: Context) : this(context,0)
    constructor(context: Context, themeResId: Int) : super(context, R.style.btm_dialog){
        setContentView(R.layout.dialog_bottom_up)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}