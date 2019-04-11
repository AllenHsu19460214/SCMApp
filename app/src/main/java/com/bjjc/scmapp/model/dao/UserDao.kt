package com.bjjc.scmapp.model.dao

import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.DeviceBean
import com.bjjc.scmapp.model.bean.LoginBean
import com.bjjc.scmapp.presenter.impl.LoginPresenterImpl
import com.bjjc.scmapp.util.MD5Utils
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Allen on 2019/04/11 10:39
 */
class UserDao:Callback<LoginBean>{
    override fun onFailure(call: Call<LoginBean>, t: Throwable) {
        doAsync {
            Thread.sleep(2000)
            uiThread {
                LoginPresenterImpl.sCallback.onFailure(t)
            }
        }
    }

    override fun onResponse(call: Call<LoginBean>, response: Response<LoginBean>) {
       LoginPresenterImpl.sCallback.onResponse(response.body()as LoginBean)
    }

    fun login(username: String, password: String, deviceBean:DeviceBean){
        RetrofitUtils.getRetrofit(App.base_url).create(ServiceApi::class.java)
            .login(
                "1",
                username,
                MD5Utils.md5Encode(password),
                MD5Utils.md5Encode(deviceBean.imei),
                deviceBean.sign,
                "PDA",
                "0"
            ).enqueue(this)
    }

}