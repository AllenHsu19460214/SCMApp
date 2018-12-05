package com.bjjc.scmapp.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.bjjc.scmapp.R
import java.io.InputStream
import java.util.*

/**
 * Created by Allen on 2018/12/04 10:48
 */
class UrlUtils {
    /**
     * 获取开发模式
     *
     * */
    fun getDevModelValue(context: Context, key: String): String {
        val property = Properties()
        val input: InputStream = context.resources.openRawResource(R.raw.config)
        property.load(input)
        property.getProperty("BASE_URL_DEBUG")
        val appInfo: ApplicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        var msg = "DEBUG"//默认是开发模式
        //获取meta-data 属性
        //获取meta-data 下面DEV_MODEL 的值
        msg = appInfo.metaData.get("DEV_MODEL") as String
        //将Application里面配置的（pro，test，debug）拼接成 UPDATE_PHOTO_URL _TEST
        val configKey: String = key + "_" + msg
        //获取配置文件对应的值
        return property.getProperty(configKey.toUpperCase())
    }
}