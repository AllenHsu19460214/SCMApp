package com.bjjc.scmapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
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

    companion object {
        lateinit var menuItems: ArrayList<MenuItem>
        const val SETTING: String = "设置"
        const val SEARCH: String = "搜索"
        const val SEARCH_BY_BILL_STATUS: String = "按以下单据状态查询:"
        const val BILL_STATUS_APPROVE: String = "已审确认"
        const val BILL_STATUS_PASS: String = "已审通过"
        const val BILL_STATUS_UNDONE: String = "未出完"
        const val BILL_STATUS_ALL: String = "显示全部"
    }

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
    fun initCenterOutSendToolBar() {
        initToolbar()
        setTitle("配送单出库")
        setToolBarNavigation()
    }

    /**
     * To initialize toolbar for CenterOutSendDetailActivity.
     */
    fun initCenterOutSendDetailToolBar() {
        initToolbar()
        setTitle("出库", "扫描信息")
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
            (context as BaseActivity).onBackPressed()
        }
    }

    /**
     * Sets menu.
     */
    fun setToolBarMenu(menuItemShow: ArrayList<String>): Toolbar {
        toolbar.inflateMenu(R.menu.menu_main)
        val menu = toolbar.menu
        menuItems = ArrayList()
        menuItems.add(menu.findItem(R.id.setting))
        menuItems.add(menu.findItem(R.id.searchView))
        menuItems.add(menu.findItem(R.id.searchByBillStatus))
        menuItems.add(menu.findItem(R.id.billStatusAll))
        menuItems.add(menu.findItem(R.id.billStatusApprove))
        menuItems.add(menu.findItem(R.id.billStatusPass))
        menuItems.add(menu.findItem(R.id.billStatusUndone))
        menuItems.add(menu.findItem(R.id.wipeCache))
        menuItems.forEach {
            it.isVisible = menuItemShow.contains(it.title)
        }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.setting -> {
                    //Toast.makeText(toolbar.context,"点击了设置按钮",Toast.LENGTH_SHORT).show()
                    //goto setting activity.
                    toolbar.context?.startActivity(Intent(toolbar.context, SettingActivity::class.java))
                }
            }
            true
        }
        return toolbar
    }

    fun setSearchView(): SearchView? {
        var searchView:SearchView?=null
        menuItems.forEach {
            if (it.title==ToolbarManager.SEARCH) {
                searchView = it.actionView as SearchView
            }
        }
        searchView?.isSubmitButtonEnabled = true
        setUnderLineTransparent(searchView!!)
        searchView?.queryHint = "输入或扫描单据号码"
        searchView?.setOnSearchClickListener {
            if (CenterOutSendActivity.scanNumber != null) {
                searchView?.setQuery(CenterOutSendActivity.scanNumber, false)
            }
        }
        searchView?.onQueryTextFocusChange { v, hasFocus ->
            if (!hasFocus) {
                if (CenterOutSendActivity.scanNumber != null) {
                    CenterOutSendActivity.scanNumber = null
                }
            }
        }
        return searchView
    }

    /**
     * Sets title.
     */
    fun setTitle(role: String, trueName: String) {
        toolbar.title = role
        toolbar.subtitle = trueName
        //toolbarTitle.text = "$role : $trueName"
    }

    fun setTitle(title: String) {
        toolbar.title = title
        //toolbarTitle.text = "$role : $trueName"
    }

    /**设置SearchView下划线透明 */
    private fun setUnderLineTransparent(searchView: SearchView) {
        try {
            val argClass = searchView.javaClass
            // mSearchPlate是SearchView父布局的名字
            val ownField = argClass.getDeclaredField("mSearchPlate")
            ownField.isAccessible = true
            val mView = ownField.get(searchView) as View
            mView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }


}