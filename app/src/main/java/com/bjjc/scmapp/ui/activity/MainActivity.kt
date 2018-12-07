package com.bjjc.scmapp.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.MainGridViewAdapter
import com.bjjc.scmapp.model.bean.SfBean
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ToolbarManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find






class MainActivity : BaseActivity(), ToolbarManager {
    override val context: Context by lazy { this }
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { find<TextView>(R.id.toolbar_title) }
    private lateinit var phoneRoleData: Array<String>
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initData() {
        val sfBean = intent.getSerializableExtra("sfBean") as SfBean
        initMainToolBar(sfBean.role, sfBean.truename)//Sets toolbar title.
        //phoneRoleData=出库,入库,货品查询,盘库,货品信息,台帐,分单
        phoneRoleData = sfBean.phoneRoleData.split(",").toTypedArray()
        gvMain.adapter = MainGridViewAdapter(this, phoneRoleData)
    }

    override fun onBackPressed() {
        val alertDialog=AlertDialog.Builder(this,R.style.appalertdialog)
            .setMessage("你确定要退出登录吗？")
            .setTitle("对话框")
            .setPositiveButton("确定") { _, _ ->
                finish()
            }
            .setNegativeButton("取消", null)
            .create()
        alertDialog.show()

        //修改“确认”、“取消”按钮的字体大小
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize = 20F
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).textSize = 20F
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        /*
        try {
            val mAlert = AlertDialog::class.java.getDeclaredField("mAlert")
            mAlert.isAccessible = true
            val mAlertController = mAlert.get(alertDialog)
            //通过反射修改title字体大小和颜色
            val mTitle = mAlertController.javaClass.getDeclaredField("mTitleView")
            mTitle.isAccessible = true
            val mTitleView = mTitle.get(mAlertController) as TextView
            mTitleView.textSize = 32f
            mTitleView.setTextColor(Color.RED)
            //通过反射修改message字体大小和颜色
            val mMessage = mAlertController.javaClass.getDeclaredField("mMessageView")
            mMessage.isAccessible = true
            val mMessageView = mMessage.get(mAlertController) as TextView
            mMessageView.textSize = 28f
            mMessageView.setTextColor(Color.GREEN)
        } catch (e1: IllegalAccessException) {
            e1.printStackTrace()
        } catch (e2: NoSuchFieldException) {
            e2.printStackTrace()
        }
        */

    }
}


