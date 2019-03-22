package com.bjjc.scmapp.presenter.impl

import android.content.Context
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.DeviceBean
import com.bjjc.scmapp.model.bean.LoginBean
import com.bjjc.scmapp.presenter.interf.LoginPresenter
import com.bjjc.scmapp.ui.activity.MainActivity
import com.bjjc.scmapp.util.*
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import com.bjjc.scmapp.view.LoginView
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Allen on 2019/01/04 13:14
 */
class LoginPresenterImpl(val context:Context,var loginView:LoginView) :LoginPresenter{
    private val deviceBean by lazy { DeviceBean() }
    override fun login(username:String,password:String) {
        checkUserInfo(username, password)
    }
    /**
     * get some Information of current device.
     */
    override fun getDeviceInfo() {
        //Obtain the phone IMEI.
        if(App.isPDA){
            deviceBean.imei= MobileInfoUtil.getIMEI(context)
        }else{
            //This is imei of handheld.
            deviceBean.imei= "355128005784806"
        }
        //deviceBean.imei= "862460034821507"
        //Obtain the Key form SD card.
        deviceBean.sign= getSign.readTxtFile(ResourcePathUtils.getSDRootPath(), "${context.packageName}/Key/sign.key")
    }

    /**
     * Checks the information of inputting by user.
     */
    private fun checkUserInfo(username:String, password:String) {
        if (checkStringUtils.checkUserName(username)){
            if (checkStringUtils.checkPassWord(password)) {
                if (!App.offLineFlag) loginIn(username, password)else loginInOffline()
            }else{
                loginView.onError("请确保密码符合以下规则:\n(长度在6~20之间，只能包含字母、数字)")
            }
        }else{
            loginView.onError("请确保帐号符合以下规则:\n(已字母开头，长度在5-16之间，可以包含字母、数字和下划线)")
        }
    }
    //Sending request of login to server.
    private fun loginIn(username: String, password: String) {
        val progressDialog = ProgressDialogUtils.showProgressDialog(context, "正在登录中!")
            RetrofitUtils.getRetrofit(App.base_url).create(ServiceApi::class.java)
                .login(
                    "1",
                    username,
                    MD5Utils.md5Encode(password),
                    MD5Utils.md5Encode(deviceBean.imei),
                    deviceBean.sign,
                    "PDA",
                    "0"
                ).enqueue(object : Callback<LoginBean> {
                    override fun onFailure(call: Call<LoginBean>, t: Throwable) {
                        doAsync {
                            Thread.sleep(2000)
                            uiThread {
                                if (progressDialog.isShowing) {
                                    progressDialog.dismiss()
                                    loginView.onError(t.message)
                                }
                            }
                        }
                    }

                    override fun onResponse(call: Call<LoginBean>, response: Response<LoginBean>) {
                        // 判断等待框是否正在显示
                        if (progressDialog.isShowing) {
                            progressDialog.dismiss()// 关闭等待框
                        }
                        App.loginBean = response.body() as LoginBean
                        if (App.loginBean.code == "08") {
                            ToastUtils.showToastS(context, App.loginBean.msg)
                            App.userIdentityBean = App.loginBean.sf
                            context.startActivity<MainActivity>("UserIdentityBean" to App.userIdentityBean )
                        } else {
                            loginView.onError(App.loginBean.msg)
                        }
                    }

                })
    }
    private fun loginInOffline() {
        val loginBeanJson = readFileUtils.getFromAssets(context, "offline/login.json")
        App.loginBean = Gson().fromJson<LoginBean>(loginBeanJson, LoginBean::class.java)
        if (App.loginBean.code == "08") {
            loginView.onError(App.loginBean.msg)
            App.userIdentityBean = App.loginBean.sf
            context.startActivity<MainActivity>("UserIdentityBean" to App.userIdentityBean )
        }
    }

}