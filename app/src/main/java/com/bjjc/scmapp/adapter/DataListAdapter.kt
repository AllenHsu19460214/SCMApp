package com.bjjc.scmapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.view.CenterOutSendDetailView
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/13 11:24
 */
class DataListAdapter(val context: Context?, private val centerOutSendDetailView: CenterOutSendDetailView) : BaseAdapter() {
    private val data: ArrayList<CenterOutSendDetailBean> by lazy { ArrayList<CenterOutSendDetailBean>() }
    companion object {
        var noCodeCount: String = "0"
    }

    fun updateData(data: ArrayList<CenterOutSendDetailBean>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }
    @SuppressLint("InflateParams", "SetTextI18n", "ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var reuseView = convertView
        val viewHolder: DataListAdapter.ViewHolder
        if (reuseView == null) {
            reuseView = LayoutInflater.from(context)
                .inflate(R.layout.layout_adpitem_center_out_send_data_list, null)
            viewHolder = ViewHolder(reuseView)
            reuseView!!.tag = viewHolder
        } else {
            viewHolder = reuseView.tag as DataListAdapter.ViewHolder
        }
        viewHolder.tvOrderNumber.text = data[position].原始订单号
        viewHolder.tvSpareNumber.text = data[position].备件编号
        viewHolder.tvPlanBoxNum.text = data[position].计划箱数.toString()
        viewHolder.tvScanCodeNum.text = data[position].出库箱数.toString()
        if (data[position].是否允许扫描 != 0) {
            viewHolder.llNoCodeNum.visibility = View.GONE
        } else {
            viewHolder.llNoCodeNum.visibility = View.VISIBLE
            viewHolder.etNoCodeNum.setText(data[position].允许输入箱数.toString())
        }
        var noCodeTotal:Long = 0
        for (value in data){
            noCodeTotal +=value.允许输入箱数
        }
        centerOutSendDetailView.setNoCodeText(noCodeTotal)
        return reuseView
    }

    override fun getItem(position: Int): Any? {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(private val reuseView: View) {
        val tvOrderNumber: TextView by lazy { reuseView.find<TextView>(R.id.tvOrderNumber) }
        val tvSpareNumber: TextView by lazy { reuseView.find<TextView>(R.id.tvSpareNumber) }
        val tvPlanBoxNum: TextView by lazy { reuseView.find<TextView>(R.id.tvPlanBoxNum) }
        val tvScanCodeNum: TextView by lazy { reuseView.find<TextView>(R.id.tvScanCodeNum) }
        val etNoCodeNum: EditText by lazy { reuseView.find<EditText>(R.id.etNoCodeNum) }
        val llNoCodeNum: LinearLayout by lazy { reuseView.find<LinearLayout>(R.id.llNoCodeNum) }

    }
}