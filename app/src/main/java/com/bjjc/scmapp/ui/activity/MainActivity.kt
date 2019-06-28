package com.bjjc.scmapp.ui.activity

import android.os.Handler
import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.UserBean
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.ui.adapter.MainGridAdapter
import com.bjjc.scmapp.ui.widget.dialog_custom.DialogDirector
import com.bjjc.scmapp.ui.widget.dialog_custom.impl.DialogBuilderYesNoImpl
import com.bjjc.scmapp.util.ProgressDialogUtils
import com.bjjc.scmapp.util.SPUtils
import com.bjjc.scmapp.util.toolbar.ToolbarManager
import kotlinx.android.synthetic.main.layout_aty_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity() {
    private var userBean: UserBean? = null
    private lateinit var myToolbar: ToolbarManager.MyToolbar
    /**
     * Loads layout of current activity.
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_main

    override fun initView() {

    }

    override fun initData() {
        userBean = App.sUserBean
        if (null!=userBean){
            myToolbar = ToolbarManager.Build(this@MainActivity, toolbar)
                .setTitle(userBean!!.role, userBean!!.truename)
                .create()

            /**
             * 出库,入库,货品查询,盘库,货品信息,台帐,分单
             */
            gvMain.adapter = MainGridAdapter(this@MainActivity).setData(userBean!!.phoneRoleData.split(","))
        }


    }

    /**
     * Pressed goBack Keypad.
     */
    override fun onBackPressed() {
        //Prompt to sign out.
        DialogDirector.showDialog(
            dialogBuilder = DialogBuilderYesNoImpl(this),
            title = "提示",
            message = "您确定要退出登录吗?",
            actionPositive = { finish() }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val itemId = intArrayOf(R.id.wipeCacheAll)
        myToolbar.setMenuItem(R.menu.menu_main, *itemId)
        myToolbar.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.wipeCacheAll -> {
                    wipeCacheAll()
                }
            }
            true
        }
        return true
    }

    private fun wipeCacheAll() {
        DialogDirector.showDialog(
            DialogBuilderYesNoImpl(this),
            "提示",
            "您确定要清除全部缓存吗?\n请谨慎清除!",
            {
                SPUtils.clearOrder()
                //The progress bar for waiting is showed and is closed after 1000 milliseconds.
                val progressDialog = ProgressDialogUtils.showProgressDialog(this, "正在清除缓存中!")
                Handler().postDelayed({ progressDialog.dismiss() }, 1000)
            }
        )
    }
}


