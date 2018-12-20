package com.bjjc.scmapp.ui.fragment

import android.view.View
import android.widget.ListView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.DataListAdapter
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.ui.activity.CenterOutSendDetailActivity
import com.bjjc.scmapp.ui.fragment.base.BaseFragment
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/13 11:18
 */
@Suppress("UNCHECKED_CAST")
class DataListFragment : BaseFragment(), DataListAdapter.IOnUpdateCountTotalListener{

    private var data: List<CenterOutSendDetailBean>? = null
    private var dataListView: View? = null
    private val dataListAdapter: DataListAdapter by lazy { DataListAdapter(activity!!) }
    private var iOnUpdateCountTotalListener:IOnUpdateCountTotalListener?=null
    private val lvDataList:ListView by lazy { dataListView!!.find<ListView>(R.id.lvDataList) }
    companion object {
        var i:Int=0
    }
    override fun initView(): View? {
        dataListView = View.inflate(context, R.layout.fragment_data_list, null)
        return dataListView
    }

    override fun initListener() {
            (activity as CenterOutSendDetailActivity).setIOnUpdateScanCountListener(
                object :CenterOutSendDetailActivity.IOnUpdateScanCountListener{
                    override fun OnUpdateScanCount() {
                        dataListAdapter.notifyDataSetChanged()
                    }

                }
            )
    }
    override fun initData() {

        data = arguments?.getSerializable("data") as List<CenterOutSendDetailBean>
        dataListAdapter.setData(data)
        dataListAdapter.setOnUpdateCountTotalListener(this)
        lvDataList.adapter = dataListAdapter
    }

    fun setOnUpdateCountTotalListener(iOnUpdateCountTotalListener:IOnUpdateCountTotalListener){
        this.iOnUpdateCountTotalListener=iOnUpdateCountTotalListener
    }
    override fun onUpdateCountTotal(mData: ArrayList<HashMap<String, String>>?) {
        iOnUpdateCountTotalListener?.onUpdateCountTotal(mData)
    }
    interface IOnUpdateCountTotalListener{
        fun onUpdateCountTotal(mData: ArrayList<HashMap<String, String>>?)
    }
}