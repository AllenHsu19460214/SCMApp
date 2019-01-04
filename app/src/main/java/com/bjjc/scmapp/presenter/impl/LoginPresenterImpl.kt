package com.bjjc.scmapp.presenter.impl

import android.content.Context
import android.content.Intent
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.DeviceBean
import com.bjjc.scmapp.model.vo.LoginVo
import com.bjjc.scmapp.presenter.interf.LoginPresenter
import com.bjjc.scmapp.ui.activity.MainActivity
import com.bjjc.scmapp.util.*
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import com.bjjc.scmapp.view.LoginView
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
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
        checkLoginInfo(username, password)
    }
    /**
     * get some Information of current device.
     */
    override fun getDeviceInfo() {
        //Obtain the phone IMEI.
        deviceBean.imei= MobileInfoUtil.getIMEI(context)
        //Obtain the Key form SD card.
        deviceBean.sign= getSign.readTxtFile(ResourcePathUtils.getSDRootPath(), "${context.packageName}/Key/sign.key")
    }

    /**
     * Checks the information of inputting by user.
     */
    private fun checkLoginInfo(username:String, password:String) {
        if (checkStringUtils.checkUserName(username)){
            if (checkStringUtils.checkPassWord(password)) {
                if (App.offLineFlag){
                    getUserInfoOffLine()
                }else{
                    getUserInfoFromServer(username, password)
                }
            }else{
                loginView.onError("请确保密码符合以下规则:\n(长度在6~20之间，只能包含字母、数字)")
            }
        }else{
            loginView.onError("请确保帐号符合以下规则:\n(已字母开头，长度在5-16之间，可以包含字母、数字和下划线)")
        }
    }
    //Sending request of login to server.
    private fun getUserInfoFromServer(username: String, password: String) {
        val progressDialog = ProgressDialogUtils.showProgressDialog(context, "正在登录中!")
            RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
                .login(
                    "1",
                    username,
                    MD5Utils.md5Encode(password),
                    MD5Utils.md5Encode(deviceBean.imei),
                    deviceBean.sign,
                    "PDA",
                    "0"
                ).enqueue(object : Callback<LoginVo> {
                    override fun onFailure(call: Call<LoginVo>, t: Throwable) {
                        doAsync {
                            Thread.sleep(2000)
                            uiThread {
                                // 判断等待框是否正在显示
                                if (progressDialog.isShowing) {
                                    progressDialog.dismiss()// 关闭等待框
                                    loginView.onError(t.message)
                                }
                            }
                        }
                    }

                    override fun onResponse(call: Call<LoginVo>, response: Response<LoginVo>) {
                        // 判断等待框是否正在显示
                        if (progressDialog.isShowing) {
                            progressDialog.dismiss()// 关闭等待框
                        }
                        //myToast(response.body().toString())
                        App.loginVo = response.body() as LoginVo
                        //info { loginVo }
                        if (App.loginVo?.code == "08") {
                            ToastUtils.showShortToast(context, App.loginVo?.msg)
                            App.sfBean = App.loginVo?.sf
                            gotoMainActivity()
                        } else {
                            loginView.onError(App.loginVo?.msg)
                        }
                    }

                })
    }
    private fun getUserInfoOffLine() {
        val loginVoJson = readFileUtils.getFromAssets(context, "offline/login.json")
        val gson = Gson()
        App.loginVo = gson.fromJson<LoginVo>(loginVoJson, LoginVo::class.java)
        if (App.loginVo?.code == "08") {
            loginView.onError(App.loginVo!!.msg)
            App.sfBean = App.loginVo?.sf
            gotoMainActivity()
        }
    }

    /**
     * 跳转到主页面
     */
    private fun gotoMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("UserIdentityBean", App.sfBean)
        context.startActivity(intent)
    }
}