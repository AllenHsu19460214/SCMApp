package com.bjjc.scmapp.ui.adapter

import android.content.Context
import android.content.Intent
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.InByTransferDocAty
import com.bjjc.scmapp.ui.activity.IntoCenterAty

/**
 * Created by Allen on 2018/12/04 14:57
 */
class CenterInModeGridAdapter(override var context: Context) : GridBaseAdapter(context) {
    override fun whichItemSelected(tag: String) {
        when (tag) {
            "中心库入库" -> {
                val intent = Intent(context, IntoCenterAty::class.java)
                context.startActivity(intent)
            }
            "移库入库" -> {
                context.startActivity(Intent(context, InByTransferDocAty::class.java))
            }
        }
    }

    override fun getButtonResourceId(str: String): Int {
        return when (str) {
            "中心库入库" -> R.drawable.selector_center_in
            "移库入库" -> R.drawable.selector_center_in_transfer
            else -> {
                R.drawable.selector_center_in
            }
        }
    }
}
