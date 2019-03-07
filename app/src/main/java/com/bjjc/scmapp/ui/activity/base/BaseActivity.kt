package com.bjjc.scmapp.ui.activity.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.bjjc.scmapp.R
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Created by Allen on 2018/11/28 14:14
 * BaseClass of all of Activities
 */
abstract class BaseActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(getLayoutId()) //Set layout of view for current activity with ID.
        applyPermission()
        initView() //Initialize widget object on current activity.
        initListener() //Set listener for widget.
        initData()   //Initialize data.
    }

    private fun applyPermission() {
        XXPermissions.with(this)
            //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
            //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
            //.permission(Permission.Group.STORAGE, Permission.Group.CALENDAR) //不指定权限则自动获取清单中的危险权限
            .request(object : OnPermission {
                override fun hasPermission(granted: List<String>, isAll: Boolean) {

                }
                override fun noPermission(denied: List<String>, quick: Boolean) {
                    if(quick) {
                        ToastUtils.show("被永久拒绝授权，请手动授予权限")
                        //如果是被永久拒绝就跳转到应用权限系统设置页面
                        //XXPermissions.gotoPermissionSettings(this@BaseActivity)
                    }else {
                        ToastUtils.show("获取权限失败")
                    }
                }
            })
    }

    /**
     * To return ID of corresponding layout.
     */
    abstract fun getLayoutId(): Int

    /**
     * To initialize widget view.
     */
    protected open fun initView() {

    }

    /**
     * To set Listener for widget view.
     */
    protected open fun initListener() {
    }

    /**
     *  To initialize corresponding data.
     */
    protected open fun initData() {
    }

    /**
     * To show Toast
     */
    protected fun myToast(msg: String) {
        runOnUiThread { toast(msg) }
    }

    /**
     * To skip to other activity and close current activity.
     */
    inline fun <reified T : BaseActivity> startActivityAndFinish() {
        startActivity<T>()
        finish()
    }

    //To check if a navigation bar exists.
    @SuppressLint("PrivateApi")
    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs = context.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {
            //TODO handle exception message show.
        }
        return hasNavigationBar
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }
}
