package com.bjjc.scmapp.ui.activity

import android.content.Context
import android.support.v7.widget.Toolbar
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterOutSendMxBean
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ToolbarManager
import kotlinx.android.synthetic.main.layout_aty_center_out_send_reduce_box.*
import org.jetbrains.anko.find


class CenterOutSendReduceBoxActivity : BaseActivity(), ToolbarManager {
    //==============================================FieldStart===================================================================
    override val context: Context by lazy { this }
    override val toolbar: Toolbar by lazy { find<Toolbar>(com.bjjc.scmapp.R.id.toolbar) }
    //==============================================FieldEnd=====================================================================
    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send_reduce_box
    override fun initView() {
        if (!App.isPDA) {
            btnScan.visibility = View.VISIBLE
        }
    }
    override fun initData() {
        //Sets toolbar title.
        initToolBar("减箱")
        val datum = intent.extras?.getSerializable("datum")as CenterOutSendMxBean
        tvWaybillNumber.text = datum.单号
    }
}
