package com.bjjc.scmapp.ui.activity

import android.preference.PreferenceManager
import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.toolbar.ToolbarManager
import kotlinx.android.synthetic.main.toolbar.*

class SettingAty : BaseActivity(){
    private lateinit var myToolbar: ToolbarManager.MyToolbar
    override fun getLayoutId(): Int =R.layout.layout_aty_setting
    override fun initData() {
        myToolbar = ToolbarManager.Build(this,toolbar)
            .setTitle("设置界面")
            .create()
        //Gets whether push notifications are selected.
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val pushNotice = sp.getBoolean("push_notice",false)
        myToast("pushNotice=$pushNotice")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

}
