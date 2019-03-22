package com.bjjc.scmapp.adapter

import android.content.Context
import android.content.Intent
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.CenterInActivity
import com.bjjc.scmapp.ui.activity.CenterInTransferActivity

/**
 * Created by Allen on 2018/12/04 14:57
 */
class CenterInModeGridAdapter(override var context: Context) : GridBaseAdapter(context) {
    override fun whichItemSelected(tag: String) {
        when (tag) {
            "中心库入库" -> {
                val intent = Intent(context, CenterInActivity::class.java)
                context.startActivity(intent)
            }
            "移库入库" -> {
                context.startActivity(Intent(context, CenterInTransferActivity::class.java))
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
