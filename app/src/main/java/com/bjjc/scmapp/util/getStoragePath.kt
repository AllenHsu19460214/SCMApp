package com.bjjc.scmapp.util

import android.content.Context
import android.content.Context.STORAGE_SERVICE
import android.os.storage.StorageManager
import java.lang.reflect.InvocationTargetException


/**
 * 通过反射调用获取内置存储和外置sd卡根路径(通用)
 *
 * @param mContext    上下文
 * @param is_removale 是否可移除，false返回内部存储，true返回外置sd卡
 * @return
 */
fun getStoragePath(mContext: Context, is_removale: Boolean): String? {
    val mStorageManager = mContext.getSystemService(STORAGE_SERVICE) as StorageManager
    val storageVolumeClazz: Class<*>?
    try {
        storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
        val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
        val getPath = storageVolumeClazz!!.getMethod("getPath")
        val isRemovable = storageVolumeClazz.getMethod("isRemovable")
        val result = getVolumeList.invoke(mStorageManager)
        val resultList = (result as Array<*>).toList()
        val length = resultList.size
        for (i in 0 until length) {
            val storageVolumeElement = resultList[i]
            val path = getPath.invoke(storageVolumeElement) as String
            val removable = isRemovable.invoke(storageVolumeElement) as Boolean
            if (is_removale == removable) {
                return path
            }
        }
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }

    return null
}