package com.bjjc.scmapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.interfac.IOnItemClickListener
import com.bjjc.scmapp.model.bean.CenterOutSendBean
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/12 13:57
 */
class CenterOutSendListAdapter(var context: Context) : BaseAdapter() {
    private  var data: ArrayList<CenterOutSendBean> = ArrayList()
    private var iOnItemClickListener : IOnItemClickListener? = null
    fun updateData(data: List<CenterOutSendBean>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }
    fun setOnItemClicked( iOnItemClickListener :IOnItemClickListener?){
        this.iOnItemClickListener = iOnItemClickListener
    }
    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var reuseView = convertView
        val viewHolder: CenterOutSendListAdapter.ViewHolder
        if (reuseView == null) {
            reuseView = LayoutInflater.from(context).inflate(R.layout.layout_adpitem_center_out_send_list, null)
            viewHolder = ViewHolder(reuseView)
            reuseView!!.tag = viewHolder
        } else {
            viewHolder = reuseView.tag as CenterOutSendListAdapter.ViewHolder
        }
        viewHolder.tvOrderStatus.text = data[position].单据状态
        viewHolder.tvOrderPriority.text = data[position].加急级别
        viewHolder.tvInOrganization.text = formatInOrganizationText(data[position].入库单位)
        viewHolder.tvOrderNumber.text = data[position].单号
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

    inner class ViewHolder(view: View) {
         val tvOrderStatus: TextView by lazy { view.find<TextView>(R.id.tvOrderStatus) }
        val tvOrderPriority: TextView by lazy { view.find<TextView>(R.id.tvOrderPriority) }
        val tvInOrganization: TextView by lazy { view.find<TextView>(R.id.tvPutInInstitution) }
        val tvOrderNumber: TextView by lazy { view.find<TextView>(R.id.tvOrderNumber) }

    }
    fun formatInOrganizationText(str:String):String{
        val strs = str.split(",")
        val strBuilder:StringBuilder= java.lang.StringBuilder()
        for(value in strs){
            strBuilder.append(value)
            strBuilder.append("\n")
        }
        strBuilder.deleteCharAt(strBuilder.length-1)
        return strBuilder.toString()
    }
}