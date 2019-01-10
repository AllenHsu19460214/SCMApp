package com.bjjc.scmapp.ui.fragment

import android.view.View
import android.widget.ListView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.DataListAdapter
import com.bjjc.scmapp.common.IntentKey
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.ui.activity.CenterOutSendDetailActivity
import com.bjjc.scmapp.ui.fragment.base.BaseFragment
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/13 11:18
 */
@Suppress("UNCHECKED_CAST")
class DataListFragment : BaseFragment() {
    private lateinit var data: ArrayList<CenterOutSendDetailBean>
    private lateinit var dataListView: View
    //private val activity:CenterOutSendDetailActivity by lazy { activity}
    private lateinit var lvDataList: ListView
    val dataListAdapter: DataListAdapter by lazy { DataListAdapter(context, activity as CenterOutSendDetailActivity) }

    override fun initView(): View? {
        dataListView = View.inflate(context, R.layout.layout_fragment_center_out_send_data_list, null)
        lvDataList = dataListView.find(R.id.lvDataList)
        return dataListView
    }

    override fun initData() {
        data =
                arguments?.getSerializable(IntentKey.CENTER_OUT_SEND_AND_DATA_LIST_FRAGMENT_ORDERDATACHANGED) as ArrayList<CenterOutSendDetailBean>
        dataListAdapter.updateData(data)
        lvDataList.adapter = dataListAdapter
    }

}