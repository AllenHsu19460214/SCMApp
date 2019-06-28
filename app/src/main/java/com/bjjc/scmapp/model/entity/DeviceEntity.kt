package com.bjjc.scmapp.model.entity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.bjjc.scmapp.R
import com.bjjc.scmapp.util.DeviceUtils

/**
 * Created by Allen on 2019/01/04 13:53
 */
@SuppressLint("StaticFieldLeak")
object DeviceEntity {
    lateinit var context: Context
    var imei: String = ""
    var sign: String = ""//Key in the SdCard
    var model: String = ""//PDA or Phone
    var isPDA:Boolean = false
    fun context(context: Context) :DeviceEntity{

        context.let {
            this.context = it
            this.model =  if (it.getString(R.string.deviceModel) == Build.MODEL)
            {
                isPDA = true
                this.imei = DeviceUtils.getIMEI(it) ?: ""
                "PDA"
            }else {
                isPDA = false
                this.imei = "355128005784806" //"862460034821507"
                "PHONE"
            }
            this.sign= DeviceUtils.getSign(DeviceUtils.getSDRootPath(), "${VersionEntity.packageName}/Key/sign.key")?:""
        }
        return this
    }
}