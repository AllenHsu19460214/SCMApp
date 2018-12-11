package com.bjjc.scmapp.adapter

import android.content.Context
import android.content.Intent
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.CenterPutInActivity
import com.bjjc.scmapp.ui.activity.CenterTransferOrderPutInActivity

/**
 * Created by Allen on 2018/12/04 14:57
 */
class RuKuModeChoiceGridViewAdapter(override var context: Context) : GridViewBaseAdapter(context) {
    override fun whichItemSelected(tag: Any) {
        when (tag) {
            "中心库入库" -> {
                val intent = Intent(context, CenterPutInActivity::class.java)
                context.startActivity(intent)
            }
            "移库入库" -> {
                context.startActivity(Intent(context, CenterTransferOrderPutInActivity::class.java))
            }
        }
    }

    override fun getButtonResourceId(str: String): Int {
        return when (str) {
            "中心库入库" -> R.drawable.selector_center_putin
            "移库入库" -> R.drawable.selector_transfer_putin
            else -> {
                R.drawable.selector_center_putin
            }
        }
    }
}
