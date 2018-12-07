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
import com.bjjc.scmapp.ui.activity.CenterPutInActivity
import com.bjjc.scmapp.ui.activity.CenterTransferOrderPutInActivity
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/04 14:57
 */
class RuKuModeChoiceGridViewAdapter(var context: Context, private var data: Array<String>) : BaseAdapter() {
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
                "中心库入库" -> {
                    val intent=Intent(context, CenterPutInActivity::class.java)
                    context.startActivity(intent)
                }
                "移库入库" -> {
                    context.startActivity(Intent(context, CenterTransferOrderPutInActivity::class.java))
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
            "中心库入库" -> R.drawable.selector_center_putin
            "移库入库" -> R.drawable.selector_transfer_putin
            else -> {
                R.drawable.selector_center_putin
            }
        }
    }
}
