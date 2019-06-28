package com.bjjc.scmapp.ui.activity

import android.app.Dialog
import android.view.Menu
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.extension.dp2px
import com.bjjc.scmapp.model.entity.NetEntity
import com.bjjc.scmapp.model.entity.VersionEntity
import com.bjjc.scmapp.presenter.base.LoginBasePresenter
import com.bjjc.scmapp.presenter.impl.LoginPresenterImpl
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.ui.view.IView
import com.bjjc.scmapp.util.getStoragePath
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.layout_aty_login.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.startActivity
import java.io.File
import kotlin.system.exitProcess


class LoginActivity : BaseActivity(), View.OnClickListener, IView {

    private val TAG = LoginActivity::class.java.simpleName
    private val loginPresenter: LoginBasePresenter by lazy { LoginPresenterImpl(this) }
    /**The exitTime is used to press the return key twice in two seconds to exit the program.* */
    private var exitTime: Long = 0
    private lateinit var progressDialog: Dialog
    override fun getLayoutId(): Int = R.layout.layout_aty_login

    override fun initView() {
        //The paddingBottom is increased by 50,if the device has a virtual key.
        if (hasNavigationBar(this)) {
            ll_login.setPadding(0, 0, 0, 50.dp2px(this))
        }
    }

    override fun initListener() {
        btn_login_submit.setOnClickListener(this)
        cb_offLine.setOnClickListener(this)
    }

    override fun initData() {
        if (NetEntity.isNetworkConnected()){
            loginPresenter.getUri()
        }
        //Sets the version name and development model.
        tvVerNameAndDevModel.text = VersionEntity.nameAndModel()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login_submit -> {
                async {
                    val extSDCardPath = getStoragePath(this@LoginActivity,false)
                    val file = File("$extSDCardPath/uuuttt.txt")
                    file.writeText("fafasfdasfdf")
                }


//                progressDialog = ProgressDialogUtils.showProgressDialog(this, "正在登录中!")
//                loginPresenter.login(etLoginUsername.text.toString(), etLoginPassword.text.toString())
            }
            R.id.cb_offLine -> {
                VersionEntity.isOffline = cb_offLine.isChecked
            }
        }
    }

    override fun onDataSuccess(data:Map<String,Any>) {
        ToastUtils.show(data["msg"])
        if (progressDialog.isShowing) progressDialog.dismiss()

//        val userDao = UserDao(this)
//        userDao.addUser(App.sUserBean!!)

        startActivity<MainActivity>()
    }

    override fun onDataFailure(data:Map<String,Any>) {
        ToastUtils.show(data["msg"])
        if (progressDialog.isShowing) progressDialog.dismiss()
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

    /**
     *  The callback of the return key from system.
     */
    override fun onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            myToast(getString(R.string.message_pressing_return_key_again))
            exitTime = System.currentTimeMillis()
        } else {
            finish()
            exitProcess(0)
        }
    }
}













