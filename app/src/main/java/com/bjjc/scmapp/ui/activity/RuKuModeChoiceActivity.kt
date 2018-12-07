package com.bjjc.scmapp.ui.activity

import android.content.Context
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.RuKuModeChoiceGridViewAdapter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ToolbarManager
import kotlinx.android.synthetic.main.activity_ruku_mode_choice.*
import org.jetbrains.anko.find

class RuKuModeChoiceActivity : BaseActivity(),ToolbarManager {
    override val context: Context by lazy { this }
    private val buttonArray: Array<String> = arrayOf("中心库入库", "移库入库")
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { find<TextView>(R.id.toolbar_title) }
    override fun getLayoutId(): Int = R.layout.activity_ruku_mode_choice
    override fun initData() {
        initRuKuModeChoiceToolBar()//Sets toolbar title.
        gvRuKuModeChoice.adapter = RuKuModeChoiceGridViewAdapter(this, buttonArray)
    }
}
