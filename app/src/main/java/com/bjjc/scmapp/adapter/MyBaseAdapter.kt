package com.bjjc.scmapp.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bjjc.scmapp.holder.MyBaseHolder

/**
 * Created by Allen on 2019/03/19 9:29
 */
abstract class MyBaseAdapter<T> : BaseAdapter() {
    private lateinit var mData: ArrayList<T>
    open fun setData(data: ArrayList<T>): BaseAdapter {
        mData = data
        return this
    }

    open fun updateData(data: ArrayList<T>) {
        mData = data
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val holder: MyBaseHolder<T> =
            if (convertView == null) {
                /**
                 * 1.Loads layout file.
                 * 2.Initialize the component.
                 * 3.Make a mark.
                 */
                getHolder()
            } else {
                @Suppress("UNCHECKED_CAST")
                convertView.tag as MyBaseHolder<T>
            }
        //4.Refresh the interface according to the data.
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