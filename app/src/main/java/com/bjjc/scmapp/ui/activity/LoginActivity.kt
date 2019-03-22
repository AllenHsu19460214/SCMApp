package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.view.Menu
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.extension.dp2px
import com.bjjc.scmapp.presenter.impl.LoginPresenterImpl
import com.bjjc.scmapp.presenter.interf.LoginPresenter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.view.LoginView
import kotlinx.android.synthetic.main.layout_aty_login.*
import org.jetbrains.anko.info


class LoginActivity : BaseActivity(), View.OnClickListener, LoginView {
    //==============================================FieldStart=====================================================================
    /**
     * The tag of the current activity
     */
    private val TAG = LoginActivity::class.java.simpleName
    /**
     * The presenter of the login activity.
     */
    private val loginPresenter: LoginPresenter by lazy { LoginPresenterImpl(this, this) }
    /**
     * The exitTime is used to press the return key twice in two seconds to exit the program.
     */
    private var exitTime: Long = 0
    //==============================================FieldEnd=====================================================================
    /**
     * Loading the layout of the current activity.
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_login

    /**
     *  Initialize the current view.
     */
    override fun initView() {
        //Determine whether the device has a virtual key,if so,add 50dp to original value of "paddingBottom".
        if (checkDeviceHasNavigationBar(this)) {
            ll_login.setPadding(0, 0, 0, 50.dp2px(this))
        }
        val username = etLoginUsername.text
        info { username }
    }

    /**
     *  Initialize listeners of components.
     */
    override fun initListener() {
        btnLoginSubmit.setOnClickListener(this)
        cbOffLine.setOnClickListener(this)
    }

    /**
     * Initialize data.
     */
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

    /**
     *  The callback of the onError from the interface LoginView.
     */
    override fun onError(message: String?) {
        message?.let { myToast(message) }
    }

    /**
     * The callback of the menu option created from system.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /*
         menuInflater.inflate(R.menu.menu_login, menu)
         */
        return true
    }
    /*
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //Key of the menu.
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // monitor or intercept key of the menu.
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
    */

    /**
     *  The callback of the return key from system.
     */
    override fun onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            myToast(getString(R.string.message_pressing_return_key_again))
            exitTime = System.currentTimeMillis()
        } else {
            finish()
            System.exit(0)
        }
    }
}













