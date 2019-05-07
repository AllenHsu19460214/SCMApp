package com.bjjc.scmapp.ui.activity

import android.content.Intent
import android.hardware.barcode.Scanner
import android.location.Location
import android.util.Log
import android.view.KeyEvent
import com.bjjc.scmapp.R
import com.bjjc.scmapp.manager.QRCodeScanner
import com.bjjc.scmapp.manager.ScannerManager
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.FeedbackUtils
import com.bjjc.scmapp.util.GpsUtils
import com.common.zxing.CaptureActivity
import kotlinx.android.synthetic.main.layout_aty_about.*



class AboutActivity : BaseActivity() {
    //========================================Field==================================================================================
    companion object {
        private val TAG: String = AboutActivity::class.java.simpleName
    }

    private var mLocation: Location? = null
    private lateinit var mScanManager:ScannerManager
    //========================================Field/=================================================================================

    override fun getLayoutId(): Int = R.layout.layout_aty_about
    override fun initListener() {
        btnScan.setOnClickListener {
            startActivityForResult(Intent(this, CaptureActivity::class.java).putExtra("autoEnlarged", true), 0)
        }
    }
    override fun initData() {
        /*mLocation = GpsUtils.getGPSContacts(this)
        tv_latitude.text = mLocation?.latitude.toString()
        tv_longitude.text = mLocation?.longitude.toString()*/
        tv_latitude.text = GpsUtils.getGPSPointString()
        mScanManager = ScannerManager.instance()
        mScanManager.put("A")
        mScanManager.put("B")
        mScanManager.put("C")
        mScanManager.put("D")
        mScanManager.put("E")
        mScanManager.put("F")
        mScanManager.put("G")
        mScanManager.setOnCheckedListener(object : QRCodeScanner.Callback{
            override fun onFailure(msg: String) {
                Log.d("AboutActivity",msg)
            }

            override fun onSuccess(QRCode: String) {
                Log.d("AboutActivity2",QRCode)
            }

        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK ->
            {
                //1.Returns value of bar code scanned.
                //onScanCodeSuccess(data?.extras?.getString("result"))
                //vibrate Feedback。
                FeedbackUtils.vibrate(this, 200)
                Log.d("AboutActivity1", data?.extras?.getString("result"))
                data?.extras?.getString("result")?.let { mScanManager.put(it) }
            }
            else->{
                FeedbackUtils.vibrate(this, 200)
                //onScanCodeFailure()
                Log.d("AboutActivity",data?.extras?.getString("result"))
                data?.extras?.getString("result")?.let { mScanManager.mScanner.mOnCallback.onFailure(it) }
            }
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
}
