package com.bjjc.scmapp.util

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by Allen on 2018/12/25 13:25
 */
object readFileUtils {
    //Gets file from assets folder and read data.
     fun getFromAssets(context:Context,fileName: String): String {
        return try {
            val inputReader = InputStreamReader(context.resources.assets.open(fileName))
            val bufReader = BufferedReader(inputReader)
            var line: String
            var result: String=""
            while (true){
                line=bufReader.readLine()?:break
                result+=line
            }
            return result

        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}