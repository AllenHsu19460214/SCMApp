package com.bjjc.scmapp.ui.activity

import android.content.Context
import android.os.Handler
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.MainGridAdapter
import com.bjjc.scmapp.model.bean.UserBean
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ProgressDialogUtils
import com.bjjc.scmapp.util.SPUtils
import com.bjjc.scmapp.util.ToolbarManager
import com.bjjc.scmapp.util.UIUtils
import com.bjjc.scmapp.util.dialog_custom.DialogDirector
import com.bjjc.scmapp.util.dialog_custom.impl.DialogBuilderYesNoImpl
import kotlinx.android.synthetic.main.layout_aty_main.*
import org.jetbrains.anko.find

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : BaseActivity(), ToolbarManager {
    //==============================================Field==========================================================================
    override val context: Context by lazy { this }
    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    //==============================================/Field=========================================================================
    /**
     * Loads layout of current activity.
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_main

    override fun initData() {
        val userIdentityBean = intent.extras["UserBean"] as UserBean
        //Sets toolbar title.
        initToolBar(userIdentityBean.role, userIdentityBean.truename)
        //phoneRoleData includes 出库,入库,货品查询,盘库,货品信息,台帐,分单
        val phoneRoleData = userIdentityBean.phoneRoleData.split(",") as ArrayList<String>
        gvMain.adapter = MainGridAdapter(this).setData(phoneRoleData)
    }

    /**
     * Pressed goBack Keypad.
     */
    override fun onBackPressed() {
        //Prompt to sign out.
        DialogDirector.showDialog(
            DialogBuilderYesNoImpl(this),
            "提示",
            "您确定要退出登录吗?",
            { finish() }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val itemId = intArrayOf(R.id.wipeCacheAll)
        setToolBarMenu(R.menu.menu_main, *itemId).setOnMenuItemClickListener {
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
                SPUtils.clearOrder(UIUtils.getContext())
                //The progress bar for waiting is showed and is closed after 1000 milliseconds.
                val progressDialog = ProgressDialogUtils.showProgressDialog(this, "正在清除缓存中!")
                Handler().postDelayed({ progressDialog.dismiss() }, 1000)
            }
        )
    }
}


