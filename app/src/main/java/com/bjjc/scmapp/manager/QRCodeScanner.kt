package com.bjjc.scmapp.manager

import android.hardware.barcode.Scanner
import android.os.Handler
import android.util.Log
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by Allen on 2019/05/05 9:59
 */
class QRCodeScanner {
    var mScannedCount: Long = 0
    var mInputtedCount: Long = 0
    var mPlanTotal: Long = 0
    val mScannedQRCode: ArrayList<String> by lazy { ArrayList<String>() }
    val mScannedExceptionQRCode: ArrayList<String> by lazy { ArrayList<String>() }
    var mRetrievedCurrent: String? = null
    val executor: ExecutorService? = null
    var workQueue: Thread? = null
    lateinit var mOnCallback: Callback
    fun start(handler: Handler) {
        initSCA(handler)
    }

    private fun initSCA(handler: Handler) {
        //assignment value to handler of scanner.
        Scanner.m_handler = handler
        //Initialize the scanning head.
        Scanner.InitSCA()
    }

    inner class Container {
        val mQueue: BlockingQueue<String> by lazy { LinkedBlockingQueue<String>(500) }
        fun enqueue(QRCode: String) {
            mQueue.put(QRCode)
        }

        fun dequeue(): String {
            return mQueue.take()
        }
    }

    fun setOnCheckedListener(onCallback: Callback) {
        mOnCallback = onCallback
    }

    inner class Putter(var threadName: String = "putter", var container: Container, var QRCode: String) : Thread() {
        override fun run() {
            Log.d("AboutActivity",Thread.currentThread().name)
            container.enqueue(QRCode)
        }
    }

    inner class Taker(var threadName: String = "taker", var container: Container) : Thread() {
        override fun run() {
            while (true) {
                sleep(3000)
                if (container.mQueue.isNotEmpty()) {
                    mOnCallback.onSuccess(container.dequeue())
                }
            }
        }
    }

    /*
    private fun checkQRCode(QRCode: String) {
        RetrofitUtils.getRetrofit(App.base_url).create(ServiceApi::class.java)
            .checkQRCode(
                "18",
                mOutType,
                QRCode,
                centerOutSendDetailBean.出库单位,
                centerOutSendDetailBean.入库单位,
                centerOutSendDetailBean.单号
            ).enqueue(object : Callback<CheckQRCodeBean> {
                override fun onFailure(call: Call<CheckQRCodeBean>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            centerOutSendDetailView.onError(t.toString())
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CheckQRCodeBean>,
                    response: Response<CheckQRCodeBean>
                ) {
                    checkQRCodeBean = response.body() as CheckQRCodeBean
                    increaseQRCodeNum(QRCode)
                    getScanTotal()
                    centerOutSendDetailView.updateView(planTotal, scanToTal)
                    isReachScanTotal()

                }

            })
    }
    */
    interface Callback {
        fun onSuccess(QRCode: String)
        fun onFailure(msg: String)
    }
}