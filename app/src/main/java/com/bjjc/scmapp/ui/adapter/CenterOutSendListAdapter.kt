package com.bjjc.scmapp.ui.adapter

import android.view.View
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.adapter.holder.MyBaseHolder
import com.bjjc.scmapp.model.bean.OutByDistributionBean
import com.bjjc.scmapp.util.UIUtils
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/12 13:57
 */
class CenterOutSendListAdapter : MyBaseAdapter<OutByDistributionBean>() {
    override fun getHolder(): MyBaseHolder<OutByDistributionBean> {
        return ViewHolder()
    }

    inner class ViewHolder : MyBaseHolder<OutByDistributionBean>() {
        private lateinit var view: View
        private val tvOrderStatus: TextView by lazy { view.find<TextView>(R.id.tvOrderStatus) }
        private val tvOrderPriority: TextView by lazy { view.find<TextView>(R.id.tvOrderPriority) }
        private val tvInPlace: TextView by lazy { view.find<TextView>(R.id.tvPutInInstitution) }
        private val tvOrderNumber: TextView by lazy { view.find<TextView>(R.id.tvOrderNumber) }

        override fun initView(): View {
            view = UIUtils.inflate(R.layout.layout_adpitem_center_out_send_list)
            return view
        }

        override fun refreshView(data: OutByDistributionBean) {
            tvOrderStatus.text = data.单据状态
            tvOrderPriority.text = data.加急级别
            tvInPlace.text = formatInPlaceText(data.入库单位)
            tvOrderNumber.text = data.单号
        }

    }

    private fun formatInPlaceText(str: String): String {
        val places = str.split(",")
        val strBuilder: StringBuilder = java.lang.StringBuilder()
        for (value in places) {
            strBuilder.append(value)
            strBuilder.append("\n")
        }
        strBuilder.deleteCharAt(strBuilder.length - 1)
        return strBuilder.toString()
    }
}