package com.bjjc.scmapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.CenterDistributionOrderOutputActivity
import com.bjjc.scmapp.ui.activity.CenterOutputActivity
import com.bjjc.scmapp.ui.activity.CenterReverseOrderOutputActivity
import com.bjjc.scmapp.ui.activity.CenterTransferOrderOutputActivity
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/04 14:57
 */
class ChuKuModeChoiceGridViewAdapter(var context: Context, private var data: Array<String>) : BaseAdapter() {
    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var reuseView = convertView
        val viewHolder: ViewHolder
        if (reuseView == null) {
            viewHolder = ViewHolder()
            reuseView = LayoutInflater.from(context).inflate(R.layout.adapter_item_main_gridview, null)
            reuseView!!.tag = viewHolder
        } else {
            viewHolder = reuseView.tag as ViewHolder
        }
        val str = data[position]
        viewHolder.ivItem = reuseView.find<ImageView>(R.id.ivItem)
        viewHolder.ivItem.setImageResource(getButtonResourceId(str))
        viewHolder.ivItem.tag = str
        viewHolder.ivItem.setOnClickListener {
            when (it.tag) {
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
        return reuseView
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    internal class ViewHolder {
        lateinit var ivItem: ImageView
    }

    private fun getButtonResourceId(str: String): Int {
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
