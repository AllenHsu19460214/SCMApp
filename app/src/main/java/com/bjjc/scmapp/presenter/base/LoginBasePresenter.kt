package com.bjjc.scmapp.presenter.base

import com.bjjc.scmapp.presenter.`interface`.IPresenter

/**
 * Created by Allen on 2019/06/19 13:40
 */
abstract class LoginBasePresenter:IPresenter {
    override fun loadMoreData() {
    }
    abstract fun login(username:String, password:String)
    abstract fun getUri()
}