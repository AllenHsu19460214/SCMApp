package com.bjjc.scmapp.adapter

import android.content.Context
import android.content.Intent
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.CenterDistributionOrderOutputActivity
import com.bjjc.scmapp.ui.activity.CenterOutputActivity
import com.bjjc.scmapp.ui.activity.CenterReverseOrderOutputActivity
import com.bjjc.scmapp.ui.activity.CenterTransferOrderOutputActivity

/**
 * Created by Allen on 2018/12/04 14:57
 */
class ChuKuModeChoiceGridViewAdapter(override var context: Context) : GridViewBaseAdapter(context) {

    override fun whichItemSelected(tag: Any) {
        when (tag) {
            "配送出库" -> {
                val intent=Intent(context, CenterDistributionOrderOutputActivity::class.java)
                context.startActivity(intent)
            }
            "中心库出库" -> {
                val intent=Intent(context, CenterOutputActivity::class.java)
                context.startActivity(intent)
            }
            "移库出库" -> {
                context.startActivity(Intent(context, CenterTransferOrderOutputActivity::class.java))
            }
            "反向订单出库" -> {
                context.startActivity(Intent(context, CenterReverseOrderOutputActivity::class.java))
            }
        }
    }

    override fun getButtonResourceId(str: String): Int {
        return when (str) {
            "配送出库" -> R.drawable.selector_distribution
            "中心库出库" -> R.drawable.selector_center_output
            "移库出库" -> R.drawable.selector_transfer_output
            "反向订单出库" -> R.drawable.selector_reverse_output
            else -> {
                R.drawable.selector_distribution
            }
        }
    }

}
