package com.bjjc.scmapp.main.ui.activity

import android.support.v7.widget.Toolbar
import com.bjjc.scmapp.R
import com.bjjc.scmapp.common.BaseActivity
import com.bjjc.scmapp.login.model.bean.SfBean
import com.bjjc.scmapp.main.adapter.MainGridViewAdapter
import com.bjjc.scmapp.util.ToolbarManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find


class MainActivity : BaseActivity(),ToolbarManager {
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private lateinit var phoneRoleData:Array<String>
    override fun getLayoutId(): Int= R.layout.activity_main

    override fun initData() {
        initMainToolBar()
        val sfBean = intent.getSerializableExtra("sfBean")as SfBean
        //phoneRoleData=出库,入库,货品查询,盘库,货品信息,台帐,分单
        phoneRoleData = sfBean.phoneRoleData.split(",").toTypedArray()
        gvMain.adapter= MainGridViewAdapter(this,phoneRoleData)
    }
}

