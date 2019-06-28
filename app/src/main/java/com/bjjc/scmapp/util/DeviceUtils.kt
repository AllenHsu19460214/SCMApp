package com.bjjc.scmapp.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Created by Allen on 2019/06/14 8:10
 */
object DeviceUtils {

    fun getSign(filePath: String, fileName: String): String? = readTxtFile(filePath, fileName)
    /**
     * 读TXT文件内容
     * Obtain the Key form SD card.
     * @param filePath 文件路径(不要以 / 结尾)
     * @param fileName 文件名称（包含后缀,如：ReadMe.txt）
     * @return
     */
    @Throws(Exception::class)
    fun readTxtFile(filePath: String, fileName: String): String? {
        var result: String? = ""
        val fn = File("$filePath/$fileName")
        var fileReader: FileReader? = null
        var bufferedReader: BufferedReader? = null
        try {
            fileReader = FileReader(fn)
            bufferedReader = BufferedReader(fileReader)
            try {
                var read: String? = null
                while ({ read = bufferedReader.readLine();read }() != null) {
                    result = result + read + "\r\n"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bufferedReader?.close()
            fileReader?.close()
        }
        // println("读取出来的文件内容是：\r\n$result")
        return result
    }
    fun getSDRootPath(): String{
        var sdDir: File? = null
        val sdCardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED//判断sd卡是否已挂载
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory()//获取SD卡根目录
        }
        return "$sdDir"
    }
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