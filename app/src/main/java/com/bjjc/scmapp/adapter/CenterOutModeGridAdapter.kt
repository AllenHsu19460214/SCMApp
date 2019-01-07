package com.bjjc.scmapp.adapter

import android.content.Context
import android.content.Intent
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.CenterOutActivity
import com.bjjc.scmapp.ui.activity.CenterOutReverseActivity
import com.bjjc.scmapp.ui.activity.CenterOutSendActivity
import com.bjjc.scmapp.ui.activity.CenterOutTransferActivity

/**
 * Created by Allen on 2018/12/04 14:57
 */
class CenterOutModeGridAdapter(override var context: Context) : GridBaseAdapter(context) {
    override fun whichItemSelected(tag: Any) {
        when (tag) {
            "配送出库" -> {
                val intent=Intent(context, CenterOutSendActivity::class.java)
                context.startActivity(intent)
            }
            "中心库出库" -> {
                val intent=Intent(context, CenterOutActivity::class.java)
                context.startActivity(intent)
            }
            "移库出库" -> {
                context.startActivity(Intent(context, CenterOutTransferActivity::class.java))
            }
            "反向订单出库" -> {
                context.startActivity(Intent(context, CenterOutReverseActivity::class.java))
            }
        }
    }

    override fun getButtonResourceId(str: String): Int {
        return when (str) {
            "配送出库" -> R.drawable.selector_send
            "中心库出库" -> R.drawable.selector_center_out
            "移库出库" -> R.drawable.selector_center_out_transfer
            "反向订单出库" -> R.drawable.selector_center_out_reverse
            else -> {
                R.drawable.selector_send
            }
        }
    }

}
