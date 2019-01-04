package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.MainGridAdapter
import com.bjjc.scmapp.model.bean.UserIdentityBean
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.DialogUtils
import com.bjjc.scmapp.util.ToolbarManager
import kotlinx.android.synthetic.main.layout_aty_main.*
import org.jetbrains.anko.find

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : BaseActivity(), ToolbarManager {
    override val context: Context by lazy { this }
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private lateinit var phoneRoleData: Array<String>
    private val mainGridViewAdapter: MainGridAdapter by lazy { MainGridAdapter(this) }

    /**
     * Loads layout of current activity.
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_main

    override fun initView() {

    }

    override fun initData() {
        val userIdentityBean = intent.getSerializableExtra("UserIdentityBean") as UserIdentityBean
        //Sets toolbar title.
        initMainToolBar(userIdentityBean.role, userIdentityBean.truename)//Sets toolbar title.
        //phoneRoleData=出库,入库,货品查询,盘库,货品信息,台帐,分单
        phoneRoleData = userIdentityBean.phoneRoleData.split(",").toTypedArray()
        mainGridViewAdapter.setData(phoneRoleData)
        gvMain.adapter = mainGridViewAdapter
    }

    @SuppressLint("InflateParams")
    /**
     * Pressed goBack Keypad.
     */
    override fun onBackPressed() {
        /*
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
        */
        promptLogOut()
    }

    /**
     * Prompt to log out.
     */
    private fun promptLogOut() {
        //customDialogYesOrNo()
        DialogUtils.instance()
            .customDialogYesOrNo(this@MainActivity)
            .setTitle("提示")
            .setMessage("您确定要退出登录吗?")
            .setOnPositiveClickListener(object : DialogUtils.OnPositiveClickListener {
                override fun onPositiveBtnClicked() {
                    finish()
                }
            })
            .setOnNegativeClickListener(object : DialogUtils.OnNegativeClickListener {
                override fun onNegativeBtnClicked() {
                }
            })
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        setToolBarMenu(false)
        return true
    }

}


