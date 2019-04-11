package com.bjjc.scmapp.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.LoginBean
import com.bjjc.scmapp.model.bean.UserBean
import com.bjjc.scmapp.util.SPUtils
import com.hjq.toast.ToastUtils
import com.hjq.toast.style.ToastWhiteStyle
import java.io.InputStream
import java.util.*

/**
 * Created by Allen on 2018/11/30 14:47
 * custom Application is Used for global initialization.
 */
class App : Application() {

    private var property: Properties? = null

    companion object {
        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        private var context: Context?=null
        @JvmStatic
        private var handler: Handler? = null
        @JvmStatic
        private var mainThreadId: Int = 0

        fun getContext():Context{
            return context!!
        }
        fun getHandler():Handler{
            return handler!!
        }
        fun getMainThreadId():Int{
            return mainThreadId
        }
        var isNewApp:Boolean = true
        var deviceModel: String = ""
        var isPDA: Boolean = false
        var offLineFlag: Boolean = false
        var verName: String? = null
        var devModel: String? = null
        lateinit var base_url: String
        lateinit var loginBean: LoginBean
        lateinit var userIdentityBean: UserBean
    }

    override fun onCreate() {
        super.onCreate()
        isNewApp= SPUtils[this, "isNewApp", true] as Boolean
        initGlobalVariable()
        deviceModel = getDeviceModel()
        isPDA = isPDA()
        verName = getVerName()
        devModel = getDevModel()
        loadConfig()
        base_url = getDevModelValue("BASE_URL")
        //Initialize ToastUtils
        ToastUtils.init(this, ToastWhiteStyle())
    }

    private fun initGlobalVariable() {
        context = applicationContext
        handler = Handler()
        mainThreadId = android.os.Process.myTid()
    }

    /**
     * Load the configuration file of the development environment.
     * */
    private fun loadConfig() {
        property = Properties()
        val input: InputStream = resources.openRawResource(R.raw.config)
        property?.load(input)
    }

    /**
     * Obtain the development model.
     * */
    private fun getDevModelValue(key: String): String {
        //The default is development mode
        val msg = devModel ?: "DEBUG"
        //Splice these configuration (pro, test, debug) in the Application to UPDATE_PHOTO_URL_TEST
        val configKey: String = key + "_" + msg
        //Obtain the value in the configuration file.
        return property!!.getProperty(configKey)
    }

    /**
     * obtain the version name.
     */
    private fun getVerName(): String {
        return packageManager.getPackageInfo(packageName, 0).versionName
    }

    /**
     * Obtain the development model.
     */
    private fun getDevModel(): String {
        val appInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        //Obtain the field of meta-data.
        //Obtain the value of DEV_MODEL in the meta-data.
        return (appInfo.metaData.get("DEV_MODEL") as String).toUpperCase()
    }

    /**
     *
     */
    private fun getDeviceModel(): String {
        if (getString(R.string.deviceModel).contains(Build.MODEL)) {
            return "PDA"
        }
        return "PHONE"

    }

    private fun isPDA(): Boolean {
        if ("PDA" == getDeviceModel()) {
            return true
        }
        return false
    }

}

