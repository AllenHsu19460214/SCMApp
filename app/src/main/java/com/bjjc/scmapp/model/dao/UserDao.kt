package com.bjjc.scmapp.model.dao

import android.content.Context
import com.bjjc.scmapp.model.bean.UserBean
import com.bjjc.scmapp.model.db.DatabaseHelper


/**
 * Created by Allen on 2019/06/21 12:56
 */
class UserDao(val context: Context){

    private var userDao = DatabaseHelper.getInstance(context).getDao(UserBean::class.java)
    fun addUser(userBean:UserBean){
        userDao?.create(userBean)
    }
}