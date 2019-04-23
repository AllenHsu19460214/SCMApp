package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.barcode.Scanner
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.model.bean.CenterOutSendMxBean
import com.bjjc.scmapp.model.bean.CommonResultBean
import com.bjjc.scmapp.model.bean.ExceptionCodeInfoBean
import com.bjjc.scmapp.presenter.impl.CenterOutSendDetailPresenterImpl
import com.bjjc.scmapp.presenter.interf.CenterOutSendDetailPresenter
import com.bjjc.scmapp.ui.activity.base.BaseScannerActivity
import com.bjjc.scmapp.util.SPUtils
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.ToolbarManager
import com.bjjc.scmapp.util.UIUtils
import com.bjjc.scmapp.util.dialog_custom.DialogDirector
import com.bjjc.scmapp.util.dialog_custom.impl.DialogBuilderYesNoImpl
import com.bjjc.scmapp.view.CenterOutSendDetailView
import com.common.zxing.CaptureActivity
import kotlinx.android.synthetic.main.layout_aty_center_out_send_detail.*
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor

@SuppressLint("CommitTransaction")
class CenterOutSendDetailActivity : BaseScannerActivity(), ToolbarManager, CenterOutSendDetailView {

    //==============================================FieldStart=====================================================================
    companion object {
        //intent key
        const val INTENT_KEY_ORDER_DATA: String = "mxData"
        const val INTENT_KEY_ORDER_NUMBER: String = "orderNumber"
    }
    override val context: Context by lazy { this }
    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private lateinit var datum: CenterOutSendMxBean
    private lateinit var toolbarMenu: Toolbar
    private lateinit var centerOutSendDetailBean:CenterOutSendDetailBean
    private var mOutType: String = ""
    val mPresenter: CenterOutSendDetailPresenter by lazy {
        CenterOutSendDetailPresenterImpl(
            this,
            this
        )
    }
    //==============================================FieldEnd=====================================================================
    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send_detail

    override fun initView() {
        if (!App.isPDA) {
            btnScan.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        mPresenter.startScanQRCodeThread()
        initToolBar("出库", "扫描信息")
        tvNoCodeTotal.text = "0"
        datum = intent.getSerializableExtra(CenterOutSendActivity.INTENT_KEY_ORDER_DATUM) as CenterOutSendMxBean
        mPresenter.setInitData(datum)
        tvWaybillNumber.text = datum.单号
        if (SPUtils.contains(context, "mxData${datum.单号}")||
            SPUtils.contains(context, "exceptionCodeInfoList${datum.单号}")||
            SPUtils.contains(context, "cachedQRCodeList${datum.单号}")||
            SPUtils.contains(context, "cachedExceptionQRCodeList${datum.单号}")) {
            mPresenter.readCache()
        } else {
            //Loads data for initialize.
            mPresenter.loadData()
        }
        //Transmit datum from CenterOutSendDetailActivity to DataListFragment.
        mPresenter.transmitDataToDataListFragment()
        rbExceptionList.isChecked = true
        //Setting dataList is initially selected.
        rbDataList.isChecked = true
    }

    override fun setExceptionTitleColor(exceptionCodeInfoList: ArrayList<ExceptionCodeInfoBean>) {
        setRbtnExceptionTextColor(exceptionCodeInfoList)
    }

    override fun initListener() {
        rgListButton.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbDataList -> {
                    mPresenter.switchFragment(CenterOutSendDetailPresenterImpl.DATA_LIST_FRAGMENT).commit()
                }
                R.id.rbExceptionList -> {
                    mPresenter.switchFragment(CenterOutSendDetailPresenterImpl.EXCEPTION_LIST_FRAGMENT).commit()
                }
            }

        }
        btnSubmit.setOnClickListener {
            // Showing dialog of finished or unfinished to be used to commit or save info of OutOrder.
            if (mPresenter.isOrderFinished()) {
                DialogDirector.showDialog(
                    DialogBuilderYesNoImpl(this@CenterOutSendDetailActivity),
                    "提示",
                    "订单已完成，要提交到服务器吗?",
                    {
                        mPresenter.submitOrderInfo()
                    }
                )
            } else {
                DialogDirector.showDialog(
                    DialogBuilderYesNoImpl(this@CenterOutSendDetailActivity),
                    "提示",
                    "订单未完成，要保存到服务器吗?",
                    {
                        mPresenter.submitOrderInfo()
                    }
                )
            }
        }
        btnScan.setOnClickListener {
            startActivityForResult(Intent(context, CaptureActivity::class.java).putExtra("autoEnlarged", true), 0)
        }
    }

    override fun updateView(planTotal: Long, scanToTal: Long) {
        tvPlanBoxTotal.text = planTotal.toString()
        tvScanTotal.text = scanToTal.toString()
    }
    override fun onReturnOutType(outType: String) {
        mOutType = outType
        SPUtils.put(this,"outType${datum.单号}",outType)
    }
    override fun onScanCodeSuccess(scanCodeResult: String?) {
        mPresenter.enqueueQRCode(scanCodeResult)
    }

    override fun onScanCodeFailure() {
        ToastUtils.showToastS(context, "未识别的条码!")
    }

    override fun onError(message: String?) {
        ToastUtils.showToastS(context, message)
    }

    override fun onLoadSuccess(data: CenterOutSendDetailBean) {
        if (data.code == "10") {
            centerOutSendDetailBean=data
            //Sets order number.
            tvWaybillNumber.text = data.单号

        } else {
            ToastUtils.showToastS(context, data.msg)
        }
    }

    override fun onSetNoCodeText(noCodeTotal: Long) {
        tvNoCodeTotal.text = noCodeTotal.toString()
    }

    override fun onSubmitSuccess(data: CommonResultBean) {
        if (data.code == "08") {
            myToast(data.msg)
            //Wiping the cache which correspond to order.
            //SPUtils.remove(context, "mxData${datum.单号}")
            if (SPUtils.contains(UIUtils.getContext(), "mxData${datum.单号}")) {
                mPresenter.wipeCache()
            }
        } else {
            myToast(data.msg)
        }
    }

    /**
     * Sets text color of RadioButtonException.
     */
    private fun setRbtnExceptionTextColor(exceptionCodeInfoList: ArrayList<ExceptionCodeInfoBean>) {
        if (exceptionCodeInfoList.size > 0) {
            rbExceptionList.textColor = resources.getColor(R.color.red)
        } else {
            rbExceptionList.setTextColor(resources.getColorStateList(R.color.selector_radio_btn_textcolor))
        }
    }

    /**
     * Creates the ToolBar。
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val itemId = intArrayOf(R.id.reduceBox, R.id.wipeCache)
        toolbarMenu = setToolBarMenu(R.menu.menu_center_out_send_detail, *itemId)
        setToolbarListener()
        return true
    }

    private fun setToolbarListener() {
        val datum =Pair("datum",datum)
        val outType =Pair("outType",mOutType)
        toolbarMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.reduceBox -> {
                    //myToast("\"减箱\" is clicked!")
//                    context.startActivity<CenterOutSendReduceBoxActivity>("datum" to datum)
                    context.startActivity<CenterOutSendReduceBoxActivity>(datum,outType)
                }
                R.id.wipeCache -> {
                    mPresenter.wipeCacheByOrderId()
                }
            }
            true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.repeatCount == 0) {
            if (keyCode == 4) {
                onBackPressed()
            } else if (keyCode == 211 || keyCode == 212 || keyCode == 220 || keyCode == 221) {
                //扫描开始
                Scanner.Read()
            }
        }
        return true
    }

    override fun onDestroy() {
        CenterOutSendDetailPresenterImpl.threadExit = true
        super.onDestroy()
    }
}
