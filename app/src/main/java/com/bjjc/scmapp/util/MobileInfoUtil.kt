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
     * Obtain the IMEI of the device.
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    fun getIMEI(context: Context): String? {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            return telephonyManager.deviceId
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }
}
