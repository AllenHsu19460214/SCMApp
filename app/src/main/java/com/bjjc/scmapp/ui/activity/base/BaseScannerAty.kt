package com.bjjc.scmapp.ui.activity.base

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.barcode.Scanner
import android.os.Handler
import android.os.Message
import com.bjjc.scmapp.util.FeedbackUtils

/**
 * Created by Allen on 2018/11/28 14:14
 * BaseClass of all of Activities
 */
abstract class BaseScannerAty : BaseActivity() {

    private val handheldScanHandler = HandheldScanHandler()

    override fun onStart() {
        super.onStart()
        //assignment value to handler of scanner.
        Scanner.m_handler = handheldScanHandler
        //Initialize the scanning head.
        Scanner.InitSCA()
    }

    @SuppressLint("HandlerLeak")
    private inner class HandheldScanHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                //Reading the bar code is successful.
                Scanner.BARCODE_READ -> {
                    FeedbackUtils.vibrate(this@BaseScannerAty, 200)
                    onScanCodeSuccess(msg.obj.toString())

                }
                //Reading the bar code is failure.
                Scanner.BARCODE_NOREAD -> {
                    FeedbackUtils.vibrate(this@BaseScannerAty, 200)
                    onScanCodeFailure()
                }

            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK ->
            {
                //1.Returns value of bar code scanned.
                onScanCodeSuccess(data?.extras?.getString("result"))
                //vibrate Feedback。
                FeedbackUtils.vibrate(this@BaseScannerAty, 200)
            }
            else->{
                FeedbackUtils.vibrate(this@BaseScannerAty, 200)
                onScanCodeFailure()
            }
        }
    }
    abstract fun onScanCodeSuccess(scanCodeResult: String?)
    abstract fun onScanCodeFailure()

}
