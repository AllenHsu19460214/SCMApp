package com.bjjc.scmapp.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 * Created by Allen on 2019/01/10 10:31
 * SharedPreferences统一管理类
 */
object SPUtils {
    /**
     * 保存在手机里面的文件名（自定义）
     */
    private const val FILE_NAME = "share_data"

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param any
     */
    fun put(context: Context, key: String, any: Any) {

        val sp = getSharedPreferences(context)
        val editor = sp.edit()

        when (any) {
            is String -> editor.putString(key,any)
            is Int -> editor.putInt(key, any)
            is Boolean -> editor.putBoolean(key, any)
            is Float -> editor.putFloat(key, any)
            is Long -> editor.putLong(key, any)
            else -> editor.putString(key, any.toString())
        }
        SharedPreferencesCompat.apply(editor)
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultAny
     * @return
     */
    operator fun get(context: Context, key: String, defaultAny: Any): Any? {
        val sp = getSharedPreferences(context)

        return when (defaultAny) {
            is String -> sp.getString(key, defaultAny)
            is Int -> sp.getInt(key, defaultAny)
            is Boolean -> sp.getBoolean(key, defaultAny)
            is Float -> sp.getFloat(key, defaultAny)
            is Long -> sp.getLong(key, defaultAny)
            else -> null
        }
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    fun remove(context: Context, key: String) {
        val sp = getSharedPreferences(context)
        val editor = sp.edit()
        editor.remove(key)
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    fun clear(context: Context) {
        val sp = getSharedPreferences(context)
        val editor = sp.edit()
        editor.clear()
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    fun contains(context: Context, key: String): Boolean {
        val sp = getSharedPreferences(context)
        return sp.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    fun getAll(context: Context): Map<String, *> {
        val sp = getSharedPreferences(context)
        return sp.all
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private object SharedPreferencesCompat {
        private val sApplyMethod = findApplyMethod()

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        private fun findApplyMethod(): Method? {
            try {
                val clz = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (e: NoSuchMethodException) {
            }

            return null
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        fun apply(editor: SharedPreferences.Editor) {  //使用反射，使之兼容
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (e: IllegalArgumentException) {
            } catch (e: IllegalAccessException) {
            } catch (e: InvocationTargetException) {
            }

            editor.commit()
        }
    }

    /**
     * 存放实体类以及任意类型
     *
     * @param context 上下文对象
     * @param key
     * @param obj
     */
    fun putBean(context: Context, key: String, obj: Any) {
        if (obj is Serializable) {
            // obj必须实现Serializable接口，否则会出问题
            try {
                val baos = ByteArrayOutputStream()
                val oos = ObjectOutputStream(baos)
                oos.writeObject(obj)
                val string64 = String(Base64.encode(baos.toByteArray(), 0))
                val sp = getSharedPreferences(context)
                val editor = sp.edit()
                editor.putString(key, string64).apply()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            throw IllegalArgumentException("the obj must implement Serializble")
        }
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun getBean(context: Context, key: String): Any? {
        var obj: Any? = null
        try {
            val base64 = getSharedPreferences(context).getString(key, "")
            if (base64 == "") {
                return null
            }
            val base64Bytes = Base64.decode(base64.toByteArray(), 1)
            val bais = ByteArrayInputStream(base64Bytes)
            val ois = ObjectInputStream(bais)
            obj = ois.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return obj
    }

}