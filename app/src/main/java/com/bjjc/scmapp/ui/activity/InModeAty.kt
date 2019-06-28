package com.bjjc.scmapp.ui.activity

import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.ui.adapter.CenterInModeGridAdapter
import com.bjjc.scmapp.util.toolbar.ToolbarManager
import kotlinx.android.synthetic.main.layout_aty_center_in_mode.*
import kotlinx.android.synthetic.main.toolbar.*

class InModeAty : BaseActivity(){
    private lateinit var myToolbar:ToolbarManager.MyToolbar
    private val buttonArray: ArrayList<String> = arrayListOf("中心库入库", "移库入库")
    private val ruKuModeChoiceGridViewAdapter:CenterInModeGridAdapter by lazy {
        CenterInModeGridAdapter(this)
    }
    override fun getLayoutId(): Int = R.layout.layout_aty_center_in_mode
    override fun initData() {
        myToolbar = ToolbarManager.Build(this,toolbar)
            .setTitle("入库模式选择")
            .create()
        ruKuModeChoiceGridViewAdapter.setData(buttonArray)
        gvRuKuModeChoice.adapter = ruKuModeChoiceGridViewAdapter
    }
}
