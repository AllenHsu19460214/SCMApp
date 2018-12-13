package com.bjjc.scmapp.ui.fragment

import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.fragment.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_data_list.*

/**
 * Created by Allen on 2018/12/13 11:18
 */
class DataListFragment:BaseFragment() {
    override fun initView(): View? {
        return View.inflate(context,R.layout.fragment_data_list,null)
    }

    override fun initData() {
        lvDataList.adapter
    }
}