package com.bjjc.scmapp.ui.activity

import android.app.Dialog
import android.view.Menu
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.extension.dp2px
import com.bjjc.scmapp.presenter.impl.LoginPresenterImpl
import com.bjjc.scmapp.presenter.interf.LoginPresenter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ProgressDialogUtils
import com.bjjc.scmapp.util.UIUtils
import com.bjjc.scmapp.view.LoginView
import kotlinx.android.synthetic.main.layout_aty_login.*
import org.jetbrains.anko.startActivity


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
    private lateinit var progressDialog: Dialog
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
    }

    /**
     *  Initialize the listener of components.
     */
    override fun initListener() {
        btnLoginSubmit.setOnClickListener(this)
        cbOffLine.setOnClickListener(this)
    }

    /**
     * Initialize data.
     */
    override fun initData() {
        //Set the version name and development model for current App.
        tvVerNameAndDevModel.text = UIUtils.concatVerNameAndDevModel()
        loginPresenter.initData()
    }

    /**
     * Setting OnClickListener for Button.
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLoginSubmit -> {
                progressDialog = ProgressDialogUtils.showProgressDialog(this, "正在登录中!")
                loginPresenter.login(etLoginUsername.text.toString(), etLoginPassword.text.toString())
            }
            R.id.cbOffLine -> {
                App.offLineFlag = cbOffLine.isChecked
            }
        }
    }
    override fun onSuccess() {
        if (progressDialog.isShowing) progressDialog.dismiss()
        startActivity<MainActivity>("UserBean" to App.userIdentityBean )
    }
    /**
     *  The callback of the onError from the interface LoginView.
     */
    override fun onError(message: String?) {
        if (progressDialog.isShowing) progressDialog.dismiss()
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













