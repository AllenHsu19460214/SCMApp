package com.bjjc.scmapp.ui.activity.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.bjjc.scmapp.R
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
        initView() //Initialize widget object on current activity.
        initListener() //Set listener for widget.
        initData()   //Initialize data.
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
    override fun onBackPressed() {
       finish()
    }
}
