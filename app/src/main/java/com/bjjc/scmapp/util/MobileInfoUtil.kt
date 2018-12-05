package com.bjjc.scmapp.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager

/**
 * Created by Allen on 2018/11/30 14:12
 */
object MobileInfoUtil {
    /**
     * 获取手机IMEI
     *
     * @param context
     * @return
     */
    fun getIMEI(context: Context): String {
        try {
            //实例化TelephonyManager对象
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            //获取IMEI号
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return ""
            }
            @SuppressLint("HardwareIds")
            var imei: String? = telephonyManager.deviceId
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = ""
            }
            return imei
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }
}
