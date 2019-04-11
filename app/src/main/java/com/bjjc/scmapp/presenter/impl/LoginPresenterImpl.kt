package com.bjjc.scmapp.presenter.impl

import android.content.Context
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.DeviceBean
import com.bjjc.scmapp.model.bean.LoginBean
import com.bjjc.scmapp.model.dao.DeviceDao
import com.bjjc.scmapp.model.dao.UserDao
import com.bjjc.scmapp.presenter.interf.LoginPresenter
import com.bjjc.scmapp.ui.activity.MainActivity
import com.bjjc.scmapp.util.CheckStringUtils
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.UIUtils
import com.bjjc.scmapp.util.readFileUtils
import com.bjjc.scmapp.view.LoginView
import com.google.gson.Gson
import org.jetbrains.anko.startActivity

/**
 * Created by Allen on 2019/01/04 13:14
 */
class LoginPresenterImpl(val context: Context, private val loginView: LoginView) : LoginPresenter {

    companion object {
        val sCallback = Callback()
        lateinit var sLoginView: LoginView
    }

    private val deviceDao: DeviceDao by lazy { DeviceDao() }
    private val userDao: UserDao by lazy { UserDao() }
    private lateinit var deviceBean: DeviceBean

    override fun login(username: String, password: String) {
        checkUserInfo(username, password)
    }

    override fun initData() {
        deviceBean = deviceDao.getDevice()
        sLoginView = loginView
    }

    /**
     * Checks the information of inputting by user.
     */
    private fun checkUserInfo(username: String, password: String) {
        if (CheckStringUtils.checkUserName(username)) {
            if (CheckStringUtils.checkPassWord(password)) {
                if (!App.offLineFlag) userDao.login(username, password, deviceBean) else loginInOffline()
            } else {
                sLoginView.onError("请确保密码符合以下规则:\n(长度在6~20之间，只能包含字母、数字)")
            }
        } else {
            sLoginView.onError("请确保帐号符合以下规则:\n(已字母开头，长度在2-20之间，可以包含字母、数字和下划线)")
        }
    }

    class Callback : ICallback<LoginBean> {
        override fun onResponse(response: LoginBean) {
            App.loginBean = response
            if (App.loginBean.code == "08") {
                ToastUtils.showToastS(UIUtils.getContext(), App.loginBean.msg)
                App.userIdentityBean = App.loginBean.sf
                sLoginView.onSuccess()
            } else {
                sLoginView.onError(App.loginBean.msg)
            }
        }

        override fun onFailure(t: Throwable) {
            sLoginView.onError(t.message)
        }
    }

    interface ICallback<T> {
        fun onResponse(response: T)
        fun onFailure(t: Throwable)

    }

    //======================================================OffLine========================================================================
    private fun loginInOffline() {
        val loginBeanJson = readFileUtils.getFromAssets(context, "offline/login.json")
        App.loginBean = Gson().fromJson<LoginBean>(loginBeanJson, LoginBean::class.java)
        if (App.loginBean.code == "08") {
            sLoginView.onError(App.loginBean.msg)
            App.userIdentityBean = App.loginBean.sf
            context.startActivity<MainActivity>("UserBean" to App.userIdentityBean)
        }
    }
    //======================================================/OffLine=======================================================================
}