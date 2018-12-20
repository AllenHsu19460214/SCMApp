package com.bjjc.scmapp.adapter

import android.annotation.SuppressLint
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/13 11:24
 */
class DetailListAdapter(var context: FragmentActivity?):BaseAdapter() {
    private  var data:List<CenterOutSendDetailBean>? = null
    fun setData(data: List<CenterOutSendDetailBean>?){
       data?.let {
           this.data = data
       }
    }
    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var reuseView = convertView
        val viewHolder: DetailListAdapter.ViewHolder
        if (reuseView == null) {
            reuseView = LayoutInflater.from(context).inflate(R.layout.layout_adpitem_center_out_send_detail_list, null)
            viewHolder = ViewHolder(reuseView)
            reuseView!!.tag = viewHolder
        } else {
            viewHolder = reuseView.tag as DetailListAdapter.ViewHolder
        }
        viewHolder.tvPutInInstitution.text= data?.get(position)?.入库单位
        viewHolder.tvSpareNumber.text= data?.get(position)?.备件编号
        viewHolder.tvSpareCount.text= data?.get(position)?.计划箱数.toString()

        return reuseView
    }

    override fun getItem(position: Int): Any? {
        return data?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data?.size?:0
    }
    inner class ViewHolder(view: View) {
        val tvPutInInstitution: TextView by lazy { view.find<TextView>(R.id.tvPutInInstitution) }
        val tvSpareNumber: TextView by lazy { view.find<TextView>(R.id.tvSpareNumber) }
        val tvSpareCount: TextView by lazy { view.find<TextView>(R.id.tvSpareCount) }

    }
}