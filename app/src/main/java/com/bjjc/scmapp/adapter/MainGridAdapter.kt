package com.bjjc.scmapp.adapter

import android.content.Context
import android.content.Intent
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.*

/**
 * Created by Allen on 2018/12/04 14:57
 */
class MainGridAdapter(override var context: Context) : GridBaseAdapter(context) {

    override fun whichItemSelected(tag: String) {
        when (tag) {
            "出库" -> {
                val intent = Intent(context, CenterOutModeActivity::class.java)
                context.startActivity(intent)
            }
            "入库" -> {
                val intent = Intent(context, CenterInModeActivity::class.java)
                context.startActivity(intent)
            }
            "货品查询" -> {
                context.startActivity(Intent(context, SearchActivity::class.java))
            }
            "盘库" -> {
                context.startActivity(Intent(context, InventoryActivity::class.java))
            }
            "台帐" -> {
                context.startActivity(Intent(context, StandingBookActivity::class.java))
            }
        }
    }

    override fun getButtonResourceId(str: String): Int {
        return when (str) {
            "出库" -> R.drawable.selector_center_out_mode
            "入库" -> R.drawable.selector_center_in_mode
            "货品查询" -> R.drawable.selector_seach_goods
            "盘库" -> R.drawable.selector_inventory
            "台帐" -> R.drawable.selector_standingbook
            else -> {
                R.drawable.selector_center_out_mode
            }
        }
    }

}
