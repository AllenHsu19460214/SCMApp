package com.bjjc.scmapp.login.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.EditText
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.common.BaseActivity
import com.bjjc.scmapp.extension.dp2px
import com.bjjc.scmapp.httpUtils.RetrofitUtils
import com.bjjc.scmapp.httpUtils.ServiceApi
import com.bjjc.scmapp.login.model.vo.LoginVo
import com.bjjc.scmapp.main.ui.activity.MainActivity
import com.bjjc.scmapp.util.MD5Utils
import com.bjjc.scmapp.util.MobileInfoUtil
import com.bjjc.scmapp.util.ResourcePathUtils
import com.bjjc.scmapp.util.checkStringUtils
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.find
import org.jetbrains.anko.info
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class LoginActivity : BaseActivity(), View.OnClickListener {
    private var sign: String? = "" //Key in the SdCard
    private var imei: String = "" //IMEI of the phone.
    //input field of username
    private val etLoginUsername by lazy {
        find<EditText>(R.id.etLoginUsername)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun setView() {
        //判断设备是否由虚拟按键，如果有则增加paddingBottom=50dp
        if (checkDeviceHasNavigationBar(this)) {
            ll_login_activity.setPadding(0, 0, 0, 50.dp2px(this))
        }
        val username = etLoginUsername.text
        info { username }
    }

    override fun setListener() {
        btnLoginSubmit.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun setData() {
        //obtain the verName and devModel.
        tvVerNameAndDevModel.text="V"+App.verName+"-"+ App.devModel
        //Obtain the phone IMEI.
        imei = MobileInfoUtil.getIMEI(this)
        //Obtain the Key form SD card.
        sign = readTxtFile(ResourcePathUtils.getSDRootPath(), "$packageName/Key/sign.key")
    }

    //Sending request of login to server.
    private fun login(username: String, password: String) {
        sign?.let {
            RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
                .login(
                    "1",
                    username,
                    password,
                    MD5Utils.md5Encode(imei),
                    it,
                    "PDA",
                    "0"
                ).enqueue(object : Callback<LoginVo> {
                    override fun onFailure(call: Call<LoginVo>, t: Throwable) {

                    }

                    override fun onResponse(call: Call<LoginVo>, response: Response<LoginVo>) {
                        //myToast(response.body().toString())
                        val loginVo = response.body() as LoginVo
                        /*info { loginVo }
                        info { sf }*/
                        if (loginVo.code=="08"){
                            myToast(loginVo.msg)
                            App.sfBean = loginVo.sf
                            gotoMainActivity()
                        }else{
                            myToast(loginVo.msg)
                        }
                    }

                })
        }
    }

    /**
     * 跳转到主页面
     */
    private fun gotoMainActivity() {
        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra("sfBean",App.sfBean)
        startActivity(intent)
    }

    /**
     * Setting OnClickListener for Button.
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLoginSubmit -> {
                //Obtaining username and password.
                val username = etLoginUsername.text.toString()
                val password = etLoginPassword.text.toString()
                //Checking format of username.
                if (checkStringUtils.checkUserName(username)) {
                    info { username }
                } else {
                    myToast("请确保帐号符合以下规则:\n(已字母开头，长度在5-16之间，可以包含字母、数字和下划线)")
                    return
                }
                //Checking format of password.
                if (checkStringUtils.checkPassWord(password)) {
                    info { password }
                } else {
                    myToast("请确保密码符合以下规则:\n(长度在6~20之间，只能包含字母、数字)")
                    return
                }
                //Sending request of login to server.
                login(username, MD5Utils.md5Encode(password))
            }
        }
    }

    /**
     * 读TXT文件内容
     * Obtain the Key form SD card.
     * @param filePath 文件路径(不要以 / 结尾)
     * @param fileName 文件名称（包含后缀,如：ReadMe.txt）
     * @return
     */
    @Throws(Exception::class)
    fun readTxtFile(filePath: String, fileName: String): String? {
        var result: String? = ""
        val fn = File("$filePath/$fileName")
        var fileReader: FileReader? = null
        var bufferedReader: BufferedReader? = null
        try {
            fileReader = FileReader(fn)
            bufferedReader = BufferedReader(fileReader)
            try {
                var read: String? = null
                while ({ read = bufferedReader.readLine();read }() != null) {
                    result = result + read + "\r\n"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bufferedReader?.close()
            fileReader?.close()
        }
        // println("读取出来的文件内容是：\r\n$result")
        return result
    }
}













