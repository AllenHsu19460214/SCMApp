package com.bjjc.scmapp.ui.activity

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ToolbarManager
import org.jetbrains.anko.find

class SettingActivity : BaseActivity(),ToolbarManager {
    override val context: Context by lazy { this }
    object Constant {
        const val TAG:String= "SettingActivity"
    }
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { find<TextView>(R.id.toolbar_title) }
    override fun getLayoutId(): Int =R.layout.activity_setting
    override fun initData() {
        initSettingToolBar()
        //Gets whether push notifications are selected.
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val pushNotice = sp.getBoolean("push_notice",false)
        myToast("pushNotice=$pushNotice")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }

}
