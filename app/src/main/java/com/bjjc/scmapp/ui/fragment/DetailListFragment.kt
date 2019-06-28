package com.bjjc.scmapp.ui.fragment

import android.view.View
import android.widget.ListView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.adapter.DetailListAdapter
import com.bjjc.scmapp.model.bean.OutByDistributeDocOrderDetailBean
import com.bjjc.scmapp.ui.fragment.base.BaseFragment
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/14 9:56
 */
class DetailListFragment:BaseFragment() {
    private var data: List<OutByDistributeDocOrderDetailBean>? = null
    private var detailListView: View? = null
    private val detailListAdapter: DetailListAdapter by lazy { DetailListAdapter(activity) }
    override fun initView(): View? {
        detailListView=View.inflate(context, R.layout.layout_fragment_center_out_send_detail_list,null)
        return detailListView
    }

    override fun initData() {
        val lvDetailList=detailListView!!.find<ListView>(R.id.lvDetailList)
        arguments?.let {
            data = it.getSerializable("orderDetailList") as List<OutByDistributeDocOrderDetailBean>
        }
        detailListAdapter.setData(data)
        if (lvDetailList.adapter==null){
            lvDetailList.adapter = detailListAdapter
        }
    }
}