package com.bjjc.scmapp.app

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.UserIdentityBean
import com.bjjc.scmapp.model.vo.LoginVo
import java.io.InputStream
import java.util.*

/**
 * Created by Allen on 2018/11/30 14:47
 */
class App : Application() {

    private var property: Properties? = null

    companion object {
        val deviceModel:String?=null
        var isPDA:Boolean=false
        var offLineFlag:Boolean=false
        var verName:String? = null
        var devModel:String?=null
        var base_url: String? = null
        var loginVo:LoginVo?=null
        var sfBean:UserIdentityBean?=null
        private val INSTANCE: App by lazy { App() }
        fun getInstance(): App = INSTANCE
    }

    override fun onCreate() {
        super.onCreate()
        isPDA=getDeviceModel()
        verName = getVerName()
        devModel = getDevModel()
        loadConfig()
        base_url = getDevModelValue("BASE_URL")
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
        val msg = devModel?:"DEBUG"
        //Splice these configuration (pro, test, debug) in the Application to UPDATE_PHOTO_URL_TEST
        val configKey: String = key + "_" + msg
        //Obtain the value in the configuration file.
        return property!!.getProperty(configKey)
    }

    /**
     * obtain the version name.
     */
    private fun getVerName():String {
        return packageManager.getPackageInfo(packageName, 0).versionName
    }
    /**
     * Obtain the development model.
     */
    private fun getDevModel():String{
        val appInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        //Obtain the field of meta-data.
        //Obtain the value of DEV_MODEL in the meta-data.
        return  (appInfo.metaData.get("DEV_MODEL") as String).toUpperCase()
    }
    /**
     *
     */
    private fun getDeviceModel():Boolean{
        devModel= Build.MODEL
        devModel?.let {
            return  getString(R.string.deviceModel).contains(it)
        }
        return false
    }

}

