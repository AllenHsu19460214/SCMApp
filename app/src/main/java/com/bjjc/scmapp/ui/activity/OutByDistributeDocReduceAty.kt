package com.bjjc.scmapp.ui.activity

import android.content.Intent
import android.hardware.barcode.Scanner
import android.view.KeyEvent
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.OutByDistributionBean
import com.bjjc.scmapp.model.entity.DeviceEntity
import com.bjjc.scmapp.presenter.`interface`.IOutByDistributeDocReducePresenter
import com.bjjc.scmapp.presenter.impl.OutByDistributeDocReducePresenterImpl
import com.bjjc.scmapp.ui.activity.base.BaseScannerAty
import com.bjjc.scmapp.ui.view.IOutByDistributeDocReduceView
import com.bjjc.scmapp.util.toolbar.ToolbarManager
import com.common.zxing.CaptureActivity
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.layout_aty_out_by_distribute_doc_reduce.*
import kotlinx.android.synthetic.main.toolbar.*


class OutByDistributeDocReduceAty : BaseScannerAty(),IOutByDistributeDocReduceView {

    private lateinit var myToolbar: ToolbarManager.MyToolbar
    var mOutType:String = ""
    val mPresenter: IOutByDistributeDocReducePresenter by lazy {
        OutByDistributeDocReducePresenterImpl(
            this,
            this
        )
    }
    override fun getLayoutId(): Int = R.layout.layout_aty_out_by_distribute_doc_reduce

    override fun initView() {
        if (!DeviceEntity.isPDA) {
            btnScan.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        myToolbar = ToolbarManager.Build(this,toolbar)
            .setTitle("减箱")
            .create()
        val datum = intent.extras?.getSerializable("datum") as OutByDistributionBean
        mOutType = intent.extras?.getString("outType") as String
        mPresenter.setInitData(datum,mOutType)
        tvWaybillNumber.text = datum.单号
    }

    override fun initListener() {
        btnScan.setOnClickListener {
            startActivityForResult(Intent(this, CaptureActivity::class.java).putExtra("autoEnlarged", true), 0)
        }
        btnSubmit.setOnClickListener {

        }
    }
    override fun updateView(reduceBoxTotal: Int) {
        numReduce.setText(reduceBoxTotal)
    }
    override fun onError(message: String?) {
        ToastUtils.show(message)
    }
    override fun onScanCodeSuccess(scanCodeResult: String?) {
        mPresenter.enqueueQRCode(scanCodeResult)
    }

    override fun onScanCodeFailure() {
        ToastUtils.show("未识别的条码!")
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
        OutByDistributeDocReducePresenterImpl.threadExit = true
        super.onDestroy()
    }
}
