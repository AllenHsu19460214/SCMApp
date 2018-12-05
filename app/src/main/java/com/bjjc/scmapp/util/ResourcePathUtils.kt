package com.bjjc.scmapp.util

import android.os.Environment
import java.io.File

/**
 * Created by Allen on 2018/11/30 9:46
 */
class ResourcePathUtils {
    companion object {
        /**
         * 获取SD卡路径
         */
        fun getSDRootPath(): String{
            var sdDir: File? = null
            val sdCardExist = Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED//判断sd卡是否已挂载
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory()//获取SD卡根目录
            }
            return "$sdDir"
        }
    }
}