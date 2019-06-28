package com.bjjc.scmapp.ui.view

/**
 * Created by Allen on 2019/06/17 16:55
 */
interface IView {
    fun onDataSuccess(data:Map<String,Any>)
    fun onDataFailure(data:Map<String,Any>)
}