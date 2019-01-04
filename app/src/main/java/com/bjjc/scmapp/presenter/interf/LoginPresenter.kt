package com.bjjc.scmapp.presenter.interf

/**
 * Created by Allen on 2019/01/04 13:14
 */
interface LoginPresenter {
    fun  getDeviceInfo()
    fun login(username:String,password:String)
}