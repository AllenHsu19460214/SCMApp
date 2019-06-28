package com.bjjc.scmapp.model.entity

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.bjjc.scmapp.util.http_util.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Allen on 2019/06/14 9:52
 */
@SuppressLint("StaticFieldLeak")
object NetEntity {
    val URL_KEY: String = "URL"
    val HOME_PATH_KEY: String = "HOME_PATH"
    var baseUrl: String = ""
    lateinit var context: Context
    lateinit var serviceController: ApiService

    fun context(context: Context): NetEntity {
        this.context = context
        baseUrl = obtainBaseUrl()
        serviceController = obtainServiceController()
        return this
    }

    private fun obtainBaseUrl(): String {
        val property = Properties()
        property.load(context.resources.openRawResource(com.bjjc.scmapp.R.raw.mode_config))
        val url = property.getProperty("${URL_KEY}_${VersionEntity.model}")
        val homePath = property.getProperty("${HOME_PATH_KEY}_${VersionEntity.model}")
        return url + homePath
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private fun obtainServiceController(): ApiService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    /**
     * Determine if there is a network connection
     */
    fun isNetworkConnected(): Boolean {
        val mConnectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mNetworkInfo = mConnectivityManager.activeNetworkInfo
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable
        }
        return false
    }

    /**
     * Determine if WIFI network is available
     */
    fun isWifiConnected(): Boolean {
        val mConnectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWiFiNetworkInfo = mConnectivityManager
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isAvailable
        }
        return false
    }

    /**
     * Determine if the MOBILE network is available
     */
    fun isMobileConnected(): Boolean {
        val mConnectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mMobileNetworkInfo = mConnectivityManager
            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (mMobileNetworkInfo != null) {
            return mMobileNetworkInfo.isAvailable
        }
        return false
    }

    /**
     * Gets information about the type of current network connection
     */
    fun getConnectedType(): Int {
        val mConnectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mNetworkInfo = mConnectivityManager.activeNetworkInfo
        if (mNetworkInfo != null && mNetworkInfo.isAvailable) {
            return mNetworkInfo.type
        }
        return -1
    }

    /**
     * Determine whether there is an outer network connection
     * (common methods cannot determine whether the outer network is connected, such as connecting to the local area network)
     */
    fun ping(): Boolean {

        var result: String? = null
        try {
            val ip = "www.baidu.com"// ping 的地址，可以换成任何一种可靠的外网
            val p = Runtime.getRuntime().exec("ping -c 3 -w 100 $ip")// ping网址3次
            // 读取ping的内容，可以不加
            val input = p.inputStream
            val ins = BufferedReader(InputStreamReader(input))
            val stringBuffer = StringBuffer()
            var content:String?
            while (true) {
                content = ins.readLine()
                if (content==null) break
                stringBuffer.append(content)
            }
            Log.d("------ping-----", "result content : ${stringBuffer.toString()}")
            // ping的状态
            val status = p.waitFor()
            if (status == 0) {
                result = "success"
                return true
            } else {
                result = "failed"
            }
        } catch (e: Exception) {
            result = "IOException"
        } catch (e: InterruptedException) {
            result = "InterruptedException"
        } finally {
            Log.d("----result---", "result = " + result!!)
        }
        return false
    }
}