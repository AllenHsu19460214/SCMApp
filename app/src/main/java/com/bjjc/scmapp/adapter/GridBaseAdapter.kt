package com.bjjc.scmapp.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.AbsListView
import android.widget.ImageView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.holder.MyBaseHolder
import com.bjjc.scmapp.util.DensityUtil
import com.bjjc.scmapp.util.UIUtils
import org.jetbrains.anko.find


/**
 * Created by Allen on 2018/12/11 10:24
 */
abstract class GridBaseAdapter(open var context: Context) : MyBaseAdapter<String>() {

    private lateinit var layoutParams: AbsListView.LayoutParams

    override fun getHolder(): MyBaseHolder<String> {
        getGridViewSizeParams()
        return ViewHolder()
    }

    private fun getGridViewSizeParams() {
        val wm = (context as Activity).windowManager
        val width = wm.defaultDisplay.width
        layoutParams = AbsListView.LayoutParams(
            (width - DensityUtil.dp2px(context, 60f)) / 2,
            (width - DensityUtil.dp2px(context, 60f)) / 2 + DensityUtil.dp2px(context, 2f)
        )
    }

    inner class ViewHolder : MyBaseHolder<String>() {
        private lateinit var view: View
        private val ivItem: ImageView by lazy { view.find<ImageView>(R.id.ivItem) }
        override fun initView(): View? {
            view = UIUtils.inflate(R.layout.layout_adpitem_main_grid)
            return view
        }

        override fun refreshView(data: String) {
            getRootView()?.layoutParams = layoutParams
            ivItem.setImageResource(getButtonResourceId(data))
            ivItem.tag = data
            ivItem.setOnClickListener {
                whichItemSelected(it.tag as String)
            }
        }
    }

    abstract fun whichItemSelected(tag: String)
    abstract fun getButtonResourceId(str: String): Int

}