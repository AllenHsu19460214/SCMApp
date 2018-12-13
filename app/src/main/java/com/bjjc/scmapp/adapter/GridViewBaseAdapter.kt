package com.bjjc.scmapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bjjc.scmapp.R
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/11 10:24
 */
abstract class GridViewBaseAdapter(open var context: Context):BaseAdapter() {
    private lateinit var data:Array<String>
    fun setData(data: Array<String>){
    this.data = data
    }
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
        viewHolder.ivItem = reuseView.find(R.id.ivItem)
        viewHolder.ivItem.setImageResource(getButtonResourceId(str))
        viewHolder.ivItem.tag = str
        viewHolder.ivItem.setOnClickListener {
            whichItemSelected(it.tag)
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

    inner class ViewHolder {
        lateinit var ivItem: ImageView
    }
    abstract fun whichItemSelected(tag:Any)
    abstract fun getButtonResourceId(str: String):Int
}