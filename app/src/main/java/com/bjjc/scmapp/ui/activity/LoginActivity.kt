package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.view.Menu
import android.view.View
import android.widget.EditText
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.extension.dp2px
import com.bjjc.scmapp.presenter.impl.LoginPresenterImpl
import com.bjjc.scmapp.presenter.interf.LoginPresenter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.view.LoginView
import kotlinx.android.synthetic.main.layout_aty_login.*
import org.jetbrains.anko.find
import org.jetbrains.anko.info


class LoginActivity : BaseActivity(), View.OnClickListener, LoginView {
    //==============================================FieldStart=====================================================================
    private val TAG = LoginActivity::class.java.simpleName
    private val loginPresenter: LoginPresenter by lazy { LoginPresenterImpl(this, this) }
    private var exitTime: Long = 0
    //input field of user_name
    private val etLoginUsername by lazy { find<EditText>(R.id.etLoginUsername) }
    //==============================================FieldEnd=====================================================================
    /**
     * Loading the layout of current "activity".
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_login

    override fun initView() {
        //判断设备是否由虚拟按键，如果有则增加paddingBottom=50dp
        if (checkDeviceHasNavigationBar(this)) {
            ll_login_activity.setPadding(0, 0, 0, 50.dp2px(this))
        }
        val username = etLoginUsername.text
        info { username }
    }

    override fun initListener() {
        btnLoginSubmit.setOnClickListener(this)
        cbOffLine.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        //Set the version name and development model for current App.
        tvVerNameAndDevModel.text = "V" + App.verName + "-" + App.devModel + "-" + if (App.isPDA) "PDA" else "PHONE"
        loginPresenter.getDeviceInfo()
    }

    /**
     * Setting OnClickListener for Button.
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLoginSubmit -> {
                //Obtaining user_name and password.
                val username = etLoginUsername.text.toString()
                val password = etLoginPassword.text.toString()
                loginPresenter.login(username, password)
            }
            R.id.cbOffLine -> {
                App.offLineFlag = cbOffLine.isChecked
            }
        }
    }

    override fun onError(message: String?) {
        message?.let { myToast(message) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /*
         val inflater = menuInflater
         inflater.inflate(R.menu.menu_login, menu)
         */
        return true
    }
    /*
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //MENU键
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //监控/拦截菜单键
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
    */

    override fun onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            myToast("再按一次返回键，退出SCM业务交互系统程序!")
            exitTime = System.currentTimeMillis()
        } else {
            finish()
            System.exit(0)
        }
    }
}













