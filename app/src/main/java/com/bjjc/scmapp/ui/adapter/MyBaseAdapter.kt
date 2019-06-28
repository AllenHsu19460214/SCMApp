package com.bjjc.scmapp.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bjjc.scmapp.ui.adapter.holder.MyBaseHolder

/**
 * Created by Allen on 2019/03/19 9:29
 */
abstract class MyBaseAdapter<T> : BaseAdapter() {
    private lateinit var mData: ArrayList<T>
    open fun setData(data: List<T>): BaseAdapter {
        mData = data as ArrayList<T>
        return this
    }

    open fun updateData(data: List<T>) {
        mData = data as ArrayList<T>
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val holder: MyBaseHolder<T> =
            if (convertView == null) {
                getHolder()
            } else {
                //MyUtils.cast(convertView.tag)
                @Suppress("UNCHECKED_CAST")
                convertView.tag as MyBaseHolder<T>
            }
        holder.setData(getItem(position))
        return holder.getRootView()
    }

    abstract fun getHolder(): MyBaseHolder<T>

    override fun getItem(position: Int): T {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mData.size
    }
}