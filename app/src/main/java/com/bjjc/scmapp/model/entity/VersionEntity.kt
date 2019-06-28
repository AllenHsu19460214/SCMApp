package com.bjjc.scmapp.model.entity

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.bjjc.scmapp.util.SPUtils

/**
 * Created by Allen on 2019/06/14 8:51
 */
@SuppressLint("StaticFieldLeak")
object VersionEntity {
    lateinit var context: Context
    var name: String = ""
    var code: String = ""
    var model: String = ""
    var packageName: String = ""
    lateinit var packageManager: PackageManager
    lateinit var appInfo: ApplicationInfo
    var isOffline:Boolean = false
    var isNewlyInstalled:Boolean = true

    fun context(context: Context): VersionEntity{

        context.let {
            this.context = it
            this.packageName = it.packageName
            this.packageManager = it.packageManager
        }
        packageManager.let {
            this.name = it.getPackageInfo(packageName, 0).versionName
            this.code = it.getPackageInfo(packageName, 0).versionCode.toString()
            this.appInfo = it.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            this.model = (appInfo.metaData.get("DEV_MODEL") as String).toUpperCase()
            this.isNewlyInstalled = SPUtils.get("isNewlyInstalled",true) as Boolean
        }
        return this
    }
    /**
     * Joins the name of the version and the model of development for current App.
     */
    fun nameAndModel(): String {
        return StringBuilder()
            .append("V")
            .append(this.name)
            .append("-")
            .append(this.model)
            .append("-")
            .append(DeviceEntity.model)
            .toString()
    }
}