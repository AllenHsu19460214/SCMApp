package com.bjjc.scmapp.manager

import android.annotation.SuppressLint
import android.hardware.barcode.Scanner
import android.os.Handler
import android.os.Message
import android.util.Log
import com.bjjc.scmapp.manager.QRCodeScanner.Container
import com.bjjc.scmapp.manager.QRCodeScanner.Taker
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Allen on 2019/05/05 11:32
 */
class ScannerManager private constructor() {
    companion object {
        val TAG: String = ScannerManager::javaClass.javaClass.simpleName
        var sInstance: ScannerManager? = null
        fun instance(): ScannerManager {
            if (null == sInstance) {
                @Synchronized
                if (null == sInstance) {
                    sInstance = ScannerManager()
                }
            }
            return sInstance!!
        }
    }

    var executor: ExecutorService = Executors.newSingleThreadExecutor()
    var mScanner: QRCodeScanner = QRCodeScanner()
    var mHandler: HandheldScanHandler
    var mTaker: Taker
    var mContainer: Container

    init {
        mHandler = HandheldScanHandler()
        mScanner.start(mHandler)
        mContainer = mScanner.Container()
        mTaker = mScanner.Taker(container = mContainer)
        executor.submit(mTaker)

    }

    fun setOnCheckedListener(onCallback: QRCodeScanner.Callback) {
        mScanner.setOnCheckedListener(onCallback)
    }

    fun put(QRCode: String) {
        mContainer.enqueue(QRCode)
    }

    @SuppressLint("HandlerLeak")
    inner class HandheldScanHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                //Reading the bar code is successful.
                Scanner.BARCODE_READ -> {
                    //FeedbackUtils.vibrate(this@BaseScannerActivity, 200)
                    //onScanCodeSuccess(msg.obj.toString())
                    //mOnCallback.onSuccess(msg.obj.toString())
                    put(msg.obj.toString())
                }
                //Reading the bar code is failure.
                Scanner.BARCODE_NOREAD -> {
                    //FeedbackUtils.vibrate(this@BaseScannerActivity, 200)
                    //onScanCodeFailure()
                    //mOnCallback.onFailure(msg.obj.toString())
                    Log.d(TAG, msg.obj.toString())
                    mScanner.mOnCallback.onFailure(msg.obj.toString())
                }

            }
        }
    }
}