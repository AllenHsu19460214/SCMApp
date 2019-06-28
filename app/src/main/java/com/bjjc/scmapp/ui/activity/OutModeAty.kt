package com.bjjc.scmapp.ui.activity

import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.ui.adapter.CenterOutModeGridAdapter
import com.bjjc.scmapp.util.toolbar.ToolbarManager
import kotlinx.android.synthetic.main.layout_aty_out_mode.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity

class OutModeAty : BaseActivity(){
    private lateinit var myToolbar: ToolbarManager.MyToolbar
    private val buttonArray: ArrayList<String> = arrayListOf("配送出库",  "中心库出库","移库出库", "反向订单出库")
    private val centerOutModeGridViewAdapter:CenterOutModeGridAdapter by lazy {CenterOutModeGridAdapter(this) }
    override fun getLayoutId(): Int = R.layout.layout_aty_out_mode
    override fun initData() {
        myToolbar = ToolbarManager.Build(this,toolbar)
            .setTitle("出库模式选择")
            .create()
        centerOutModeGridViewAdapter.setData(buttonArray)
        gvCenterOutMode.adapter = centerOutModeGridViewAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val itemId= intArrayOf(R.id.setting)
        myToolbar.setMenuItem(R.menu.menu_center_out_mode,*itemId).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.setting -> {
                    startActivity<SettingAty>()
                }
            }
            true
        }
        return true
    }
}
