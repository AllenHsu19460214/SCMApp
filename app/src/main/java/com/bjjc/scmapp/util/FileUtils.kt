package com.bjjc.scmapp.util

import java.io.File
import com.bjjc.scmapp.util.FileUtils.copyDirDetail as copyDirDetail1

/**
 * Created by Allen on 2019/04/04 15:51
 */
object FileUtils {
     fun delFolder(folderPath:String){
        delAllFile(folderPath)
        val myFilePath:File = File(folderPath)
        myFilePath.delete()
    }

    fun delAllFile(folderPath: String):Boolean {
        var flag:Boolean = false
        val file:File = File(folderPath)
        if (!file.exists()){
            return flag
        }
        if (!file.isDirectory){
            return flag
        }
        val tempList:Array<String> = file.list()
        for (i in 0..tempList.size){
            val temp = if (folderPath.endsWith(File.separator)){
                File(folderPath + tempList[i])
            }else{
                File(folderPath+File.separator+ tempList[i])
            }
            if (temp.isFile){
                temp.delete()
            }
            if (temp.isDirectory){
                delAllFile(folderPath+"/"+tempList[i])
                delFolder(folderPath+"/"+tempList[i])
                flag=true
            }
        }
        return flag
    }
    fun copyDir(src:File,dest:File){
        var dest1: File?=null
        if(src.isDirectory){
            dest1= File(dest, src.name)
        }
        dest1?.let{
            copyDirDetail1(src,it)
        }
    }

    private fun copyDirDetail(src:File,dest:File) {
        if (src.isFile){

        }
    }
}