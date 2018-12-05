package com.bjjc.scmapp.util

import android.support.v7.widget.Toolbar

/**
 * Created by Allen on 2018/11/29 10:37
 * The ManagerClass for all of toolbars.
 */
interface ToolbarManager {
    val toolbar:Toolbar
    /**
     * To initialize toolbar in MainActivity.
     */
    fun initMainToolBar(){
        toolbar.title = "CSM业务交互系统"
    }
}