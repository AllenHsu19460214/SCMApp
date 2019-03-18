package com.bjjc.scmapp.util.dialog_custom.impl

import android.content.Context
import com.bjjc.scmapp.R
import com.bjjc.scmapp.util.dialog_custom.IDialogBuilder

/**
 * Created by Allen on 2019/03/15 10:45
 */
class DialogBuilderYesImpl(mContext:Context): DialogBuilderImpl(mContext), IDialogBuilder{
    override fun setLayOutId() {
        mLayoutId= R.layout.layout_dialog_custom_yes
    }

}