package com.bjjc.scmapp.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions


/**
 * Created by Allen on 2019/03/04 9:45
 * desc:Obtains current information of location by GPS.
 */
object GpsUtils {
    private lateinit var lm: LocationManager
    private var mLocation: Location? = null
    private fun showGPSLocation(context: Context): Location? {
        if (Build.VERSION.SDK_INT >= 23) { //Determine whether it is android6.0 version, if yes, you need to add permissions dynamically
            if (XXPermissions.isHasPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                com.hjq.toast.ToastUtils.show("已经获取到权限，不需要再次申请了")
                mLocation = GpsUtils.getGPSContacts(context)
                return mLocation
            } else {
                applyPermission(context)
                com.hjq.toast.ToastUtils.show("还没有获取到权限或者部分权限未授予")
            }
        } else {
            return if (GpsUtils.getGPSContacts(context) != null) {
                mLocation =GpsUtils.getGPSContacts(context)
                mLocation
            } else {
                com.hjq.toast.ToastUtils.show("还没有获取到权限或者部分权限未授予")
                null
            }

        }
        return null
    }
    fun getGPSPointString():String?{
       val location= getGPSContacts(UIUtils.getContext())
        return location?.let { "${it.longitude},${it.latitude}" }
    }
    /**
     * Gets the latitude and longitude of the specific location
     * location.latitude 纬度
     * location.longitude 经度
     */
    fun getGPSContacts(context: Context): Location? { // Gets the location management service
        lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return if (ok) {
            getLastKnownLocation(context)
        } else {
            ToastUtils.showToastS(context, "GPS未开启或未开启相应权限!")
            null
        }
    }

    private fun getLastKnownLocation(context: Context): Location? {
        val providers = lm.getProviders(true)
        var bestLocation: Location? = null
        /** This code needs no further exploration，
         * and it is  generated automatically for locationManager.getLastKnownLocation(provider)，
         * If you don't add it, you're going to make a mistake
         * */
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            return null
        }
        for (provider in providers) {
            val l = lm.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }

    private fun applyPermission(context: Context) {
        XXPermissions.with(context as Activity)
            //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
            //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
            //.permission(Permission.Group.STORAGE, Permission.Group.CALENDAR) //不指定权限则自动获取清单中的危险权限
            .request(object : OnPermission {
                override fun hasPermission(granted: List<String>, isAll: Boolean) {
                    mLocation=GpsUtils.getGPSContacts(context)

                }

                override fun noPermission(denied: List<String>, quick: Boolean) {
                    if (quick) {
                        com.hjq.toast.ToastUtils.show("被永久拒绝授权，请手动授予权限")
                        //如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.gotoPermissionSettings(context)
                    } else {
                        com.hjq.toast.ToastUtils.show("获取权限失败")
                    }
                }
            })
    }
}