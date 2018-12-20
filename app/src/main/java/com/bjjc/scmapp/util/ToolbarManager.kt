package com.bjjc.scmapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.CenterOutSendActivity
import com.bjjc.scmapp.ui.activity.SettingActivity
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextFocusChange


/**
 * Created by Allen on 2018/11/29 10:37
 * The ManagerClass for all of toolbars.
 */
interface ToolbarManager {
    val toolbar: Toolbar
    val context: Context
    /**
     * To initialize toolbar for MainActivity.
     */
    @SuppressLint("SetTextI18n")
    fun initMainToolBar(role: String, trueName: String) {
        initToolbar()
        setTitle(role, trueName)
        setToolBarNavigation()
    }

    /**
     * To initialize toolbar for SettingActivity.
     */
    fun initSettingToolBar() {
        initToolbar()
        setTitle("设置界面")
        setToolBarNavigation()
    }

    /**
     * To initialize toolbar for CenterOutModeActivity.
     */
    fun initChuKuModeChoiceToolBar() {
        initToolbar()
        setTitle("出库模式选择")
        setToolBarNavigation()
    }

    /**
     * To initialize toolbar for CenterInModeActivity.
     */
    fun initRuKuModeChoiceToolBar() {
        initToolbar()
        setTitle("入库模式选择")
        setToolBarNavigation()
    }
    /**
     * To initialize toolbar for CenterOutSendActivity.
     */
    fun initCenterDistributionOrderOutputToolBar() {
        initToolbar()
        setTitle("配送单出库")
        setToolBarNavigation()
    }
    /**
     * To initialize toolbar for CenterOutSendDetailActivity.
     */
    fun initCenterDistributionOrderOutputDetailToolBar() {
        initToolbar()
        setTitle("出库","扫描信息")
        setToolBarNavigation()
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
    fun setToolBarNavigation() {
        toolbar.setNavigationOnClickListener {
            /* Toast.makeText(toolbar.context, "返回", Toast.LENGTH_SHORT).show()
             var b = ActivityUtils.isForeground(context as BaseActivity, MainActivity::class.java.name)
             (context as BaseActivity).finish()*/
            (context as BaseActivity).onBackPressed()
        }
    }

    /**
     * Sets menu.
     */
    fun setToolBarMenu(isShowSearch: Boolean):SearchView {
        toolbar.inflateMenu(R.menu.menu_main)
        val menu = toolbar.menu
        val menuItem = menu.findItem(R.id.ab_search)
        menuItem.isVisible = isShowSearch
        val searchView = menuItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "输入或扫描单据号码"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                ToastUtils.showShortToast(toolbar.context, query)
                return true
            }

            override fun onQueryTextChange(nextText: String): Boolean {
                //ToastUtils.showShortToast(toolbar.context, nextText)
                return true
            }
        })
        searchView.setOnSearchClickListener {
            if (CenterOutSendActivity.scanNumber!=null){
                searchView.setQuery(CenterOutSendActivity.scanNumber,false)
            }
        }
        searchView.onQueryTextFocusChange { v, hasFocus ->
            if (!hasFocus){
                if (CenterOutSendActivity.scanNumber!=null){
                    CenterOutSendActivity.scanNumber=null
                }
            }
        }

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
        return searchView
    }

    /**
     * Sets title.
     */
    fun setTitle(role: String, trueName: String) {
        toolbar.title = role
        toolbar.subtitle= trueName
        //toolbarTitle.text = "$role : $trueName"
    }

    fun setTitle(title: String) {
        toolbar.title = title
        //toolbarTitle.text = "$role : $trueName"
    }


}