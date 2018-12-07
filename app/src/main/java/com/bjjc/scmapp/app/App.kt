package com.bjjc.scmapp.app

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.SfBean
import java.io.InputStream
import java.util.*

/**
 * Created by Allen on 2018/11/30 14:47
 */
class App : Application() {

    private var property: Properties? = null

    companion object {
        var verName:String? = null
        var devModel:String?=null
        var base_url: String? = null
        var sfBean:SfBean?=null
        private val INSTANCE: App by lazy { App() }
        fun getInstance(): App = INSTANCE
    }

    /**
     * 获取Application
     */
    override fun onCreate() {
        super.onCreate()
        verName = getVerName()
        devModel = getDevModel()
        loadConfig()
        base_url = getDevModelValue("BASE_URL")
    }


    /**
     * 加载开发环境的配置文件
     *
     * */
    private fun loadConfig() {
        property = Properties()
        val input: InputStream = resources.openRawResource(R.raw.config)
        property?.load(input)
    }

    /**
     * 获取开发模式
     *
     * */
    private fun getDevModelValue(key: String): String {
        val msg = devModel?:"DEBUG"//默认是开发模式
        //将Application里面配置的（pro，test，debug）拼接成 UPDATE_PHOTO_URL _TEST
        val configKey: String = key + "_" + msg
        //获取配置文件对应的值
        return property!!.getProperty(configKey)
    }

    /**
     * 获取App版本名称
     */
    private fun getVerName():String {
        return packageManager.getPackageInfo(packageName, 0).versionName
    }
    /**
     * 获取App开发模式
     */
    private fun getDevModel():String{
        val appInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        //获取meta-data 属性
        //获取meta-data 下面DEV_MODEL 的值
        return  (appInfo.metaData.get("DEV_MODEL") as String).toUpperCase()
    }

}

