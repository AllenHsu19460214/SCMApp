package com.bjjc.scmapp.presenter.impl

import android.content.Context
import android.util.Log
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.UriBean
import com.bjjc.scmapp.model.bean.UserBean
import com.bjjc.scmapp.model.entity.DeviceEntity
import com.bjjc.scmapp.model.entity.NetEntity
import com.bjjc.scmapp.model.entity.UserEntity.password
import com.bjjc.scmapp.model.entity.UserEntity.username
import com.bjjc.scmapp.model.entity.VersionEntity
import com.bjjc.scmapp.presenter.base.LoginBasePresenter
import com.bjjc.scmapp.ui.view.IView
import com.bjjc.scmapp.util.CheckStringUtils
import com.bjjc.scmapp.util.MD5Utils
import com.bjjc.scmapp.util.UIUtils
import com.bjjc.scmapp.util.readFileUtils
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Allen on 2019/01/04 13:14
 */
class LoginPresenterImpl(loginView: IView) : LoginBasePresenter(), Callback<String> {

    companion object {
        //For async
        lateinit var sLoginView: IView

    }

    private val TAG = LoginPresenterImpl::class.java.simpleName
    private var context: Context

    init {
        sLoginView = loginView
        context = loginView as Context
    }

    override fun login(username: String, password: String) {
        checkUserInfo(username, password)
    }

    override fun onFailure(call: Call<String>, t: Throwable) {
        doAsync {
            Thread.sleep(2000)
            uiThread {
                t.message?.let {
                    sLoginView.onDataFailure(mapOf("msg" to it))
                }

            }
        }
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        if (response.isSuccessful) {
            val result = response.body()
            parseResult(result)
        }
    }

    private fun parseResult(result: String?) {
        val jsonObject = JSONObject(result)
        val code = jsonObject.getString("code")
        val msg = jsonObject.getString("msg")
        if ("08" == code) {
            Log.d(TAG, "$code==>$msg")
            val key = jsonObject.getString("key")
            val sf = jsonObject.getString("sf")
            val userBean: UserBean = Gson().fromJson(sf, UserBean::class.java)
            App.sKey = key
            App.sUserBean = userBean
            sLoginView.onDataSuccess(mapOf("msg" to msg))
        } else {
            Log.e(TAG, "$code==>$msg")
            sLoginView.onDataFailure(mapOf("msg" to msg))
        }
    }

    override fun getUri() {
        NetEntity.serviceController.getUri(command = "2").enqueue(object :Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let {
                    sLoginView.onDataFailure(mapOf("msg" to it))
                }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    parseUriResult(result)
                }
            }
        })
    }

    private fun parseUriResult(result: String?) {
        val jsonObject = JSONObject(result)
        if (jsonObject.has("protocol")) {
            val uriBean:UriBean = Gson().fromJson(result,UriBean::class.java)
            App.sUriBean = uriBean
        } else if (jsonObject.has("code")){
            val code = jsonObject.getString("code")
            val msg = jsonObject.getString("msg")
            Log.e(TAG, "$code==>$msg")
        }
    }

    override fun loadData() {
        login(username, password)
    }

    /**
     * Checks the information of inputting by user.
     */
    private fun checkUserInfo(username: String, password: String) {
        if (CheckStringUtils.checkUserName(username)) {
            if (CheckStringUtils.checkPassWord(password)) {
                if (!VersionEntity.isOffline) {
                    NetEntity.serviceController
                        .login(
                            command = "1",
                            username = username,
                            password = MD5Utils.md5Encode(password),
                            sbid = MD5Utils.md5Encode(DeviceEntity.imei),
                            sign = DeviceEntity.sign,
                            type = "PDA",
                            sysIndex = "0"
                        ).enqueue(this)
                } else {
                    checkUserInfoFromLocal()
                }
            } else {
                val msg = "请确保密码符合以下规则:\n(长度在6~20之间，只能包含字母、数字)"
                sLoginView.onDataFailure(mapOf("msg" to msg))
            }
        } else {
            val msg = "请确保帐号符合以下规则:\n(以字母开头，长度在2-20之间，可以包含字母、数字和下划线)"
            sLoginView.onDataFailure(mapOf("msg" to msg))
        }
    }

    //======================================================Offline========================================================================
    private fun checkUserInfoFromLocal() {
        val result= readFileUtils.getFromAssets(UIUtils.context, "offline/login.json")
        parseResult(result)
    }
    //======================================================/Offline=======================================================================
}