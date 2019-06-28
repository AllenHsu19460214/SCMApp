package com.bjjc.scmapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.ExceptionQRCodeInfoBean
import org.jetbrains.anko.find


/**
 * Created by Allen on 2018/12/13 11:24
 */
class ExceptionListAdapter(private val context: Context) : BaseAdapter() {
    private var data: List<ExceptionQRCodeInfoBean>? = null

    fun setData(data: List<ExceptionQRCodeInfoBean>?) {
        data?.let {
            this.data = data
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n", "ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView:View
        val viewHolder: ViewHolder
        if (convertView == null) {
            itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_adpitem_center_out_send_exception_list, null)
            viewHolder = ViewHolder(itemView)
            itemView.tag = viewHolder
        } else {
            itemView = convertView
            viewHolder = itemView.tag as ViewHolder
        }
        viewHolder.tvExceptionCode.text=data?.get(position)?.code
        viewHolder.tvExceptionMsg.text=data?.get(position)?.msg
        return itemView
    }

    override fun getItem(position: Int): Any? {
        return data?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data?.size ?: 0
    }
    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(private val reuseView: View) {
        val tvExceptionCode: TextView by lazy { reuseView.find<TextView>(R.id.tvExceptionCode) }
        val tvExceptionMsg: TextView by lazy { reuseView.find<TextView>(R.id.tvExceptionMsg) }

    }

}