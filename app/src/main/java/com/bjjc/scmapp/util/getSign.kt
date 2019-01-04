package com.bjjc.scmapp.util

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Created by Allen on 2019/01/04 13:48
 */
object getSign {
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
}