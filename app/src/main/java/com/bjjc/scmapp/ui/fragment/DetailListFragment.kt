package com.bjjc.scmapp.ui.fragment

import android.view.View
import android.widget.ListView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.DetailListAdapter
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.ui.fragment.base.BaseFragment
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/14 9:56
 */
class DetailListFragment:BaseFragment() {
    private var data: List<CenterOutSendDetailBean>? = null
    private var detailListView: View? = null
    private val detailListAdapter: DetailListAdapter by lazy { DetailListAdapter(activity) }
    override fun initView(): View? {
        detailListView=View.inflate(context, R.layout.fragment_detail_list,null)
        return detailListView
    }

    override fun initData() {
        val lvDetailList=detailListView!!.find<ListView>(R.id.lvDetailList)
        arguments?.let {
            data = it.getSerializable("data") as List<CenterOutSendDetailBean>
        }
        detailListAdapter.setData(data)
        if (lvDetailList.adapter==null){
            lvDetailList.adapter = detailListAdapter
        }
    }
}