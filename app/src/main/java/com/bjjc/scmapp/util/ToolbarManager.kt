package com.bjjc.scmapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.SettingActivity
import com.bjjc.scmapp.ui.activity.base.BaseActivity

/**
 * Created by Allen on 2018/11/29 10:37
 * The ManagerClass for all of toolbars.
 */
interface ToolbarManager {
    val toolbar: Toolbar
    val toolbarTitle: TextView
    val context: Context

    /**
     * To initialize toolbar for MainActivity.
     */
    @SuppressLint("SetTextI18n")
    fun initMainToolBar(role: String, trueName: String) {
        initToolbar()
        setTitle(role, trueName)
        setMenu()
        setNavigation()
    }

    /**
     * To initialize toolbar for SettingActivity.
     */
    fun initSettingToolBar() {
        initToolbar()
        setTitle("设置界面")
        setMenu()
        setNavigation()
    }

    /**
     * To initialize toolbar for ChuKuModeChoiceActivity.
     */
    fun initChuKuModeChoiceToolBar() {
        initToolbar()
        setTitle("出库模式选择")
        setMenu()
        setNavigation()
    }
    /**
     * To initialize toolbar for RuKuModeChoiceActivity.
     */
    fun initRuKuModeChoiceToolBar() {
        initToolbar()
        setTitle("入库模式选择")
        setMenu()
        setNavigation()
    }
    /**
     * initialize toolbar.
     */
    fun initToolbar() {
        (context as BaseActivity).setSupportActionBar(toolbar)
        (context as BaseActivity).supportActionBar?.setHomeButtonEnabled(true)
        (context as BaseActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (context as BaseActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /**
     * set navigation.
     */
    fun setNavigation() {

        toolbar.setNavigationOnClickListener {
            //Toast.makeText(toolbar.context, "返回", Toast.LENGTH_SHORT).show()
            //var b = ActivityUtils.isForeground(context as BaseActivity, MainActivity::class.java.name)
            //(context as BaseActivity).finish()
            (context as BaseActivity).onBackPressed()
        }
    }

    /**
     * Sets menu.
     */
    fun setMenu() {
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.setting -> {
                    //Toast.makeText(toolbar.context,"点击了设置按钮",Toast.LENGTH_SHORT).show()
                    //goto setting activity.
                    toolbar.context.startActivity(Intent(toolbar.context, SettingActivity::class.java))
                }
            }
            true
        }
    }

    /**
     * Sets title.
     */
    fun setTitle(role: String, trueName: String) {
        toolbar.title = "$role : $trueName"
        //toolbarTitle.text = "$role : $trueName"
    }

    fun setTitle(title: String) {
        toolbar.title = title
        //toolbarTitle.text = "$role : $trueName"
    }


}