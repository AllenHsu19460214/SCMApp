package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.barcode.Scanner
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.CommonInfoBean
import com.bjjc.scmapp.model.bean.ExceptionQRCodeInfoBean
import com.bjjc.scmapp.model.bean.OutByDistributeDocOrderInfoBean
import com.bjjc.scmapp.model.bean.OutByDistributionBean
import com.bjjc.scmapp.model.entity.DeviceEntity
import com.bjjc.scmapp.presenter.`interface`.IOutByDistributeDocDetailPresenter
import com.bjjc.scmapp.presenter.impl.OutByDistributeDocDetailPresenterImpl
import com.bjjc.scmapp.ui.activity.base.BaseScannerAty
import com.bjjc.scmapp.ui.view.IOutByDistributeDocDetailView
import com.bjjc.scmapp.ui.widget.dialog_custom.DialogDirector
import com.bjjc.scmapp.ui.widget.dialog_custom.impl.DialogBuilderYesNoImpl
import com.bjjc.scmapp.util.SPUtils
import com.bjjc.scmapp.util.toolbar.ToolbarManager
import com.common.zxing.CaptureActivity
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.layout_aty_out_by_distribute_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor

@SuppressLint("CommitTransaction")
class OutByDistributeDocDetailAty : BaseScannerAty(), IOutByDistributeDocDetailView {

    //==============================================FieldStart=====================================================================
    companion object {
        //intent key
        const val INTENT_KEY_ORDER_DATA: String = "mxData"
        const val INTENT_KEY_ORDER_NUMBER: String = "orderNumber"
    }
    private lateinit var myToolbar: ToolbarManager.MyToolbar
    private lateinit var datum: OutByDistributionBean
    private lateinit var centerOutSendDetailBean: OutByDistributeDocOrderInfoBean
    private var mOutType: String = ""
    val mPresenter: IOutByDistributeDocDetailPresenter by lazy {
        OutByDistributeDocDetailPresenterImpl(this, this)
    }

    //==============================================FieldEnd=====================================================================
    override fun getLayoutId(): Int = R.layout.layout_aty_out_by_distribute_detail

    override fun initView() {
        if (!DeviceEntity.isPDA) {
            btnScan.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        mPresenter.startScanQRCodeThread()
        myToolbar = ToolbarManager.Build(this,toolbar)
            .setTitle("出库", "扫描信息")
            .create()
        tvNoCodeTotal.text = "0"
        datum = intent.getSerializableExtra(OutByDistributionActivity.INTENT_KEY_ORDER_DATUM) as OutByDistributionBean
        mPresenter.setInitData(datum)
        tvWaybillNumber.text = datum.单号
        if (mPresenter.isExistCache()) {
            mPresenter.readCache()
        } else {
            //Loads data for initialize.
            mPresenter.loadData()
        }
        //Transmit datum from OutByDistributeDocDetailAty to DataListFragment.
        mPresenter.transmitDataToDataListFragment()
        rbExceptionList.isChecked = true
        //Setting dataList is initially selected.
        rbDataList.isChecked = true
    }

    override fun setExceptionTitleColor(exceptionCodeInfoList: ArrayList<ExceptionQRCodeInfoBean>) {
        setRbtnExceptionTextColor(exceptionCodeInfoList)
    }

    override fun initListener() {
        rgListButton.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbDataList -> {
                    mPresenter.switchFragment(OutByDistributeDocDetailPresenterImpl.DATA_LIST_FRAGMENT).commit()
                }
                R.id.rbExceptionList -> {
                    mPresenter.switchFragment(OutByDistributeDocDetailPresenterImpl.EXCEPTION_LIST_FRAGMENT).commit()
                }
            }

        }
        btnSubmit.setOnClickListener {
            // Showing dialog of finished or unfinished to be used to commit or save info of OutOrder.
            if (mPresenter.isOrderFinished()) {
                DialogDirector.showDialog(
                    DialogBuilderYesNoImpl(this@OutByDistributeDocDetailAty),
                    "提示",
                    "订单已完成，要提交到服务器吗?",
                    {
                        mPresenter.submitOrderInfo()
                    }
                )
            } else {
                DialogDirector.showDialog(
                    DialogBuilderYesNoImpl(this@OutByDistributeDocDetailAty),
                    "提示",
                    "订单未完成，要保存到服务器吗?",
                    {
                        mPresenter.submitOrderInfo()
                    }
                )
            }
        }
        btnScan.setOnClickListener {
            startActivityForResult(Intent(this, CaptureActivity::class.java).putExtra("autoEnlarged", true), 0)
        }
    }

    override fun updateView(planTotal: Long, scanToTal: Long) {
        tvPlanBoxTotal.text = planTotal.toString()
        tvScanTotal.text = scanToTal.toString()
    }

    override fun onReturnOutType(outType: String) {
        mOutType = outType
        SPUtils.put("outType${datum.单号}", outType)
    }

    override fun onScanCodeSuccess(scanCodeResult: String?) {
        mPresenter.enqueueQRCode(scanCodeResult)
    }

    override fun onScanCodeFailure() {
        ToastUtils.show("未识别的条码!")
    }

    override fun onError(message: String?) {
        ToastUtils.show(message)
    }

    override fun onLoadSuccess(data: OutByDistributeDocOrderInfoBean) {
        if (data.code == "10") {
            centerOutSendDetailBean = data
            //Sets order number.
            tvWaybillNumber.text = data.单号

        } else {
            ToastUtils.show(data.msg)
        }
    }

    override fun onSetNoCodeText(noCodeTotal: Long) {
        tvNoCodeTotal.text = noCodeTotal.toString()
    }

    override fun onSubmitSuccess(data: CommonInfoBean) {
        if (data.code == "08") {
            myToast(data.msg)
            //Wiping the cache which correspond to order.
            //SPUtils.remove(context, "mxData${datum.单号}")
            if (SPUtils.contains("mxData${datum.单号}")) {
                mPresenter.wipeCache()
            }
        } else {
            myToast(data.msg)
        }
    }

    /**
     * Sets text color of RadioButtonException.
     */
    private fun setRbtnExceptionTextColor(exceptionCodeInfoList: ArrayList<ExceptionQRCodeInfoBean>) {
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
        val itemId = intArrayOf(R.id.reduceBox, R.id.wipeCache,R.id.wipeExceptionCode)
        myToolbar.setMenuItem(R.menu.menu_center_out_send_detail, *itemId)
        setToolbarListener()
        return true
    }

    private fun setToolbarListener() {
        val datum = Pair("datum", datum)
        val outType = Pair("outType", mOutType)
        myToolbar.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.reduceBox -> {
                    //myToast("\"减箱\" is clicked!")
//                    context.startActivity<OutByDistributeDocReduceAty>("datum" to datum)
                    startActivity<OutByDistributeDocReduceAty>(datum, outType)
                }
                R.id.wipeCache -> {
                    mPresenter.wipeCacheByOrderId()
                }
                R.id.wipeExceptionCode->{
                    (mPresenter as OutByDistributeDocDetailPresenterImpl).wipeExceptionCode()
                }
            }
            true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.repeatCount == 0) {
            if (keyCode == 4) {
                if (OutByDistributeDocDetailPresenterImpl.exitFlag) {
                    onBackPressed()
                } else {
                    DialogDirector.showDialog(
                        DialogBuilderYesNoImpl(this@OutByDistributeDocDetailAty),
                        "提示",
                        "已扫描的箱码队列未全部处理完，您是否要终止处理?",
                        {
                            onBackPressed()
                        }
                    )
                }
            } else if (keyCode == 211 || keyCode == 212 || keyCode == 220 || keyCode == 221) {
                //扫描开始
                Scanner.Read()
            }
        }
        return true
    }

    override fun onDestroy() {
        //Log.d("CenterOutSendDetail","onDestroy")
        OutByDistributeDocDetailPresenterImpl.threadExit = true
        super.onDestroy()
    }
}
