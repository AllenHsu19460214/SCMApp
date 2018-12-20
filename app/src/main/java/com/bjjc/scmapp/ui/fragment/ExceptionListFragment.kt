package com.bjjc.scmapp.ui.fragment

import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.fragment.base.BaseFragment

/**
 * Created by Allen on 2018/12/14 9:56
 */
class ExceptionListFragment:BaseFragment() {
    override fun initView(): View? {
        return View.inflate(context, R.layout.layout_fragment_center_out_send_exception_list,null)
    }
}