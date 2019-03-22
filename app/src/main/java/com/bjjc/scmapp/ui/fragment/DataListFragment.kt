package com.bjjc.scmapp.ui.fragment

import android.view.View
import android.widget.ListView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.DataListAdapter
import com.bjjc.scmapp.model.bean.CenterOutSendDetailMxBean
import com.bjjc.scmapp.ui.activity.CenterOutSendDetailActivity
import com.bjjc.scmapp.ui.activity.CenterOutSendDetailActivity.Companion.INTENT_KEY_ORDER_DATA
import com.bjjc.scmapp.ui.activity.CenterOutSendDetailActivity.Companion.INTENT_KEY_ORDER_NUMBER
import com.bjjc.scmapp.ui.fragment.base.BaseFragment
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/13 11:18
 */
@Suppress("UNCHECKED_CAST")
class DataListFragment : BaseFragment() {
    private lateinit var data: ArrayList<CenterOutSendDetailMxBean>
    private lateinit var dataListView: View
    private lateinit var orderNumber:String
    private lateinit var lvDataList: ListView
    lateinit var dataListAdapter: DataListAdapter

    override fun initView(): View? {
        dataListView = View.inflate(context, R.layout.layout_fragment_center_out_send_data_list, null)
        lvDataList = dataListView.find(R.id.lvDataList)
        return dataListView
    }

    override fun initData() {
        orderNumber = arguments?.getString(INTENT_KEY_ORDER_NUMBER)!!
        data = arguments?.getSerializable(INTENT_KEY_ORDER_DATA) as ArrayList<CenterOutSendDetailMxBean>
        dataListAdapter =DataListAdapter(context, activity as CenterOutSendDetailActivity)
        dataListAdapter.updateData(data, orderNumber)
        lvDataList.adapter = dataListAdapter
    }
    fun updateData(data:ArrayList<CenterOutSendDetailMxBean>){
        this.data=data
        dataListAdapter.updateData(this.data, orderNumber)
    }
}