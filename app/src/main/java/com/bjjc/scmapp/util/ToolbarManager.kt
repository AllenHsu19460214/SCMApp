package com.bjjc.scmapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.CenterOutSendActivity
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
    fun initToolBar(title: String, subtitle: String) {
        initToolbar()
        setTitle(title, subtitle)
        setToolBarNavigation()
    }
    fun initToolBar(title: String) {
        initToolbar()
        setTitle(title)
        setToolBarNavigation()
    }
    /**
     * Sets title.
     */
    fun setTitle(title: String, subtitle: String) {
        toolbar.title = title
        toolbar.subtitle = title
        //toolbarTitle.text = "$role : $trueName"
    }

    fun setTitle(title: String) {
        toolbar.title = title
        //toolbarTitle.text = "$role : $trueName"
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
    fun setToolBarMenu(id:Int,vararg itemIds:Int): Toolbar {
        toolbar.inflateMenu(id)
        menuItems = ArrayList()
        for(itemId in itemIds){
            menuItems.add(toolbar.menu.findItem(itemId))
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