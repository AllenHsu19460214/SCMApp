package com.bjjc.scmapp.util

import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.bjjc.scmapp.R

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
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.setting->{
                    Toast.makeText(toolbar.context,"点击了设置按钮",Toast.LENGTH_SHORT).show()
                    //goto setting activity.
                }
            }
            true
        }
    }
}