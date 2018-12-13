package com.bjjc.scmapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.CenterDistributionOrderOutputMingXiDetailBean
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/13 11:24
 */
class DataListAdapter(var context: Context):BaseAdapter() {
    private  var data:List<CenterDistributionOrderOutputMingXiDetailBean>? = null
    fun setData(data:List<CenterDistributionOrderOutputMingXiDetailBean>){
       data.let {
           this.data = data
       }
    }
    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var reuseView = convertView
        val viewHolder: DataListAdapter.ViewHolder
        if (reuseView == null) {
            reuseView = LayoutInflater.from(context).inflate(R.layout.adapter_item_center_distribution_order_output_data_listview, null)
            viewHolder = ViewHolder(reuseView)
            reuseView!!.tag = viewHolder
        } else {
            viewHolder = reuseView.tag as DataListAdapter.ViewHolder
        }
        viewHolder.tvSpareNumber.text= data!![position].备件编号
        viewHolder.tvPlanBoxCount.text= data!![position].计划箱数.toString()
        viewHolder.tvHandleCount.text= "120"

        return reuseView
    }

    override fun getItem(position: Int): Any {
        return data!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data!!.size
    }
    inner class ViewHolder(view: View) {
        val tvSpareNumber: TextView by lazy { view.find<TextView>(R.id.tvSpareNumber) }
        val tvPlanBoxCount: TextView by lazy { view.find<TextView>(R.id.tvPlanBoxCount) }
        val tvHandleCount: TextView by lazy { view.find<TextView>(R.id.tvHandleCount) }

    }
}