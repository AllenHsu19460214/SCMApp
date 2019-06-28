package com.bjjc.scmapp.util

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


/**
 * Created by Allen on 2019/06/21 15:54
 */
object StoragePathUtil {
    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    fun getExtSDCardPath(): List<String> {
        val lResult = ArrayList<String>()
        try {
            val rt = Runtime.getRuntime()
            val proc = rt.exec("mount")
            val ins = proc.inputStream
            val isr = InputStreamReader(ins)
            val br = BufferedReader(isr)
            var line: String?
            while (true) {
                line = br.readLine()
                if (line==null) break
                if (line.contains("extSdCard")) {
                    val arr = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val path = arr[1]
                    val file = File(path)
                    if (file.isDirectory) {
                        lResult.add(path)
                    }
                }
            }
            isr.close()
        } catch (e: Exception) {
        }

        return lResult
    }
}