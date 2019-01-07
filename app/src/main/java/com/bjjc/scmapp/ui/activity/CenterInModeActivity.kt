package com.bjjc.scmapp.ui.activity

import android.content.Context
import android.support.v7.widget.Toolbar
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.CenterInModeGridAdapter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ToolbarManager
import kotlinx.android.synthetic.main.layout_aty_center_in_mode.*
import org.jetbrains.anko.find

class CenterInModeActivity : BaseActivity(),ToolbarManager {
    override val context: Context by lazy { this }
    private val buttonArray: Array<String> = arrayOf("中心库入库", "移库入库")
    private val ruKuModeChoiceGridViewAdapter:CenterInModeGridAdapter by lazy {
        CenterInModeGridAdapter(this)
    }
    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    override fun getLayoutId(): Int = R.layout.layout_aty_center_in_mode
    override fun initData() {
        initRuKuModeChoiceToolBar()//Sets toolbar title.
        ruKuModeChoiceGridViewAdapter.setData(buttonArray)
        gvRuKuModeChoice.adapter = ruKuModeChoiceGridViewAdapter
    }
}
