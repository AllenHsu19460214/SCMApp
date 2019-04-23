package com.bjjc.scmapp.ui.activity

import android.content.Context
import android.content.Intent
import android.hardware.barcode.Scanner
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterOutSendMxBean
import com.bjjc.scmapp.presenter.impl.CenterOutSendRBoxPresenterImpl
import com.bjjc.scmapp.presenter.interf.CenterOutSendRBoxPresenter
import com.bjjc.scmapp.ui.activity.base.BaseScannerActivity
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.ToolbarManager
import com.bjjc.scmapp.view.CenterOutSendRBoxView
import com.common.zxing.CaptureActivity
import kotlinx.android.synthetic.main.layout_aty_center_out_send_reduce_box.*
import org.jetbrains.anko.find


class CenterOutSendReduceBoxActivity : BaseScannerActivity(),CenterOutSendRBoxView, ToolbarManager {

    //==============================================FieldStart===================================================================
    override val context: Context by lazy { this }
    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    var mOutType:String = ""
    val mPresenter: CenterOutSendRBoxPresenter by lazy {
        CenterOutSendRBoxPresenterImpl(
            this,
            this
        )
    }
    //==============================================FieldEnd=====================================================================
    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send_reduce_box

    override fun initView() {
        if (!App.isPDA) {
            btnScan.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        //Sets toolbar title.
        initToolBar("减箱")
        val datum = intent.extras?.getSerializable("datum") as CenterOutSendMxBean
        mOutType = intent.extras?.getString("outType") as String
        mPresenter.setInitData(datum,mOutType)
        tvWaybillNumber.text = datum.单号
    }

    override fun initListener() {
        btnScan.setOnClickListener {
            startActivityForResult(Intent(context, CaptureActivity::class.java).putExtra("autoEnlarged", true), 0)
        }
        btnSubmit.setOnClickListener {

        }
    }
    override fun updateView(reduceBoxTotal: Int) {
        numReduce.setText(reduceBoxTotal)
    }
    override fun onError(message: String?) {
        ToastUtils.showToastS(context, message)
    }
    override fun onScanCodeSuccess(scanCodeResult: String?) {
        mPresenter.enqueueQRCode(scanCodeResult)
    }

    override fun onScanCodeFailure() {
        ToastUtils.showToastS(context, "未识别的条码!")
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
        CenterOutSendRBoxPresenterImpl.threadExit = true
        super.onDestroy()
    }
}
