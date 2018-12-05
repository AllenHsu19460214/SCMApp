package com.bjjc.scmapp.main.ui.activity

import com.bjjc.scmapp.R
import com.bjjc.scmapp.common.BaseActivity
import com.bjjc.scmapp.login.model.bean.SfBean
import com.bjjc.scmapp.main.adapter.MainGridViewAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private lateinit var phoneRoleData:Array<String>
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun setData() {
        val sfBean = intent.getSerializableExtra("sfBean")as SfBean
        //phoneRoleData=出库,入库,货品查询,盘库,货品信息,台帐,分单
        phoneRoleData = sfBean.phoneRoleData.split(",").toTypedArray()
        gvMain.adapter= MainGridViewAdapter(this,phoneRoleData)
    }
}

