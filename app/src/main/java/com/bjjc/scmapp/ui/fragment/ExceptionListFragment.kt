package com.bjjc.scmapp.ui.fragment

import android.view.View
import android.widget.ListView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.adapter.ExceptionListAdapter
import com.bjjc.scmapp.model.bean.ExceptionQRCodeInfoBean
import com.bjjc.scmapp.ui.fragment.base.BaseFragment
import org.jetbrains.anko.find

/**
 * Created by Allen on 2018/12/14 9:56
 */
@Suppress("UNCHECKED_CAST")
class ExceptionListFragment : BaseFragment() {
    private var data: List<ExceptionQRCodeInfoBean>? = null
    private lateinit var exceptionListView: View
    private lateinit var exceptionListAdapter: ExceptionListAdapter
    private lateinit var lvExceptionList: ListView
    override fun init() {
        exceptionListAdapter=activity?.let{ExceptionListAdapter(it)}!!
    }
    override fun initView(): View? {
        exceptionListView = View.inflate(context, R.layout.layout_fragment_center_out_send_exception_list, null)
        lvExceptionList=exceptionListView.find(R.id.lvExceptionList)
        return exceptionListView
    }

    override fun initData() {
        arguments?.let {
            data = it.getSerializable("exceptionCodeInfoList") as List<ExceptionQRCodeInfoBean>
        }
        exceptionListAdapter.setData(data)
        lvExceptionList.adapter = exceptionListAdapter
    }

    fun updateList() {
        arguments?.let {
            data = it.getSerializable("exceptionCodeInfoList") as List<ExceptionQRCodeInfoBean>
        }
        exceptionListAdapter.let {
            it.setData(data)
            it.notifyDataSetChanged()
        }


    }
}