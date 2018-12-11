package com.bjjc.scmapp.ui.activity

import android.content.Context
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.ChuKuModeChoiceGridViewAdapter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ToolbarManager
import kotlinx.android.synthetic.main.activity_chuku_mode_choice.*
import org.jetbrains.anko.find

class ChuKuModeChoiceActivity : BaseActivity(), ToolbarManager {
    override val context: Context by lazy { this }
    private val buttonArray: Array<String> = arrayOf("配送出库", "中心库出库", "移库出库", "反向订单出库")
    private val chuKuModeChoiceGridViewAdapter:ChuKuModeChoiceGridViewAdapter by lazy {
        ChuKuModeChoiceGridViewAdapter(this)
    }
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { find<TextView>(R.id.toolbar_title) }
    override fun getLayoutId(): Int = R.layout.activity_chuku_mode_choice
    override fun initData() {
        initChuKuModeChoiceToolBar()//Sets toolbar title.
        chuKuModeChoiceGridViewAdapter.setData(buttonArray)
        gvChuKuModeChoice.adapter = chuKuModeChoiceGridViewAdapter
    }
}
