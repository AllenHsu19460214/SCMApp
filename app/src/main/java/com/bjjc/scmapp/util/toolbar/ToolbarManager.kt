package com.bjjc.scmapp.util.toolbar

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextFocusChange


/**
 * Created by Allen on 2018/11/29 10:37
 */
@SuppressLint("StaticFieldLeak")
object ToolbarManager {
    val SETTING: String = "设置"
    val SEARCH: String = "搜索"
    val SEARCH_BY_BILL_STATUS: String = "按以下单据状态查询:"
    val BILL_STATUS_APPROVE: String = "已审确认"
    val BILL_STATUS_PASS: String = "已审通过"
    val BILL_STATUS_UNDONE: String = "未出完"
    val BILL_STATUS_ALL: String = "显示全部"

    interface ISearchView {
        fun onSearchClickListener(it: View)
        fun onQueryTextFocusChange(hasFocus: Boolean)
    }

    class MyToolbar(val context: Context,var toolbar:Toolbar){
        lateinit var searchView:SearchView
        private var menuItems: ArrayList<MenuItem> = ArrayList()
        /**
         * Adds menuItems.
         */
        fun setMenuItem(id: Int, vararg itemIds: Int):  Toolbar {
            toolbar.inflateMenu(id)
            for (itemId in itemIds) {
                menuItems.add(toolbar.menu.findItem(itemId))
            }
            return toolbar
        }

        fun setSearchView(queryHint: String, iSearchView: ISearchView): SearchView {
            for (menuItem in menuItems){
                if (menuItem.title == SEARCH) {
                    searchView = menuItem.actionView  as SearchView
                }
            }
            setUnderLineTransparent(searchView)

            return searchView.apply {
               isSubmitButtonEnabled = true
                setQueryHint(queryHint)
                setOnSearchClickListener {
                    iSearchView.onSearchClickListener(it)
                }
                onQueryTextFocusChange { v, hasFocus ->
                    iSearchView.onQueryTextFocusChange(hasFocus)
                }
            }
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
    class Build(val context: Context,tb:Toolbar) {
        private val baseActivity = context as BaseActivity
        private var myToolbar:MyToolbar = MyToolbar(context,tb)
        private var toolbar:Toolbar
        init {
            toolbar = myToolbar.toolbar
            initToolbar()
        }
        /**
         * set toolbar.
         */
        private fun initToolbar(): Build {
            baseActivity.apply {
                setSupportActionBar(toolbar)
                supportActionBar?.apply {
                    setHomeButtonEnabled(true)
                    setDisplayHomeAsUpEnabled(true)
                    setDisplayShowTitleEnabled(false)
                }
            }
            setToolBarNavigation()
            return this
        }

        /**
         * Sets title.
         */
        fun setTitle(title: String, subtitle: String = ""): Build {
            toolbar.title = title
            toolbar.subtitle = subtitle
            return this
        }


        /**
         * set navigation.
         */
        private fun setToolBarNavigation(): Build {
            toolbar.setNavigationOnClickListener {
                baseActivity.onBackPressed()
            }
            return this
        }




        fun create():MyToolbar{
            return myToolbar
        }

    }
}