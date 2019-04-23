package com.bjjc.scmapp.presenter.impl

import android.content.Context
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterOutSendMxBean
import com.bjjc.scmapp.model.bean.CheckQRCodeBean
import com.bjjc.scmapp.presenter.interf.CenterOutSendRBoxPresenter
import com.bjjc.scmapp.util.FeedbackUtils
import com.bjjc.scmapp.util.dialog_custom.DialogDirector
import com.bjjc.scmapp.util.dialog_custom.impl.DialogBuilderYesImpl
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import com.bjjc.scmapp.view.CenterOutSendRBoxView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Allen on 2019/04/22 11:53
 */
class CenterOutSendRBoxPresenterImpl(var context: Context, var centerOutSendReduceBoxView: CenterOutSendRBoxView) :CenterOutSendRBoxPresenter{
    //==============================================Field===========================================================
    companion object {
        var threadExit: Boolean = false
    }
    private lateinit var mDatum: CenterOutSendMxBean
    //private val cachedQRCodeList: ArrayList<String> by lazy { ArrayList<String>() }
    private var queueQRCode: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()
    private lateinit var checkQRCodeBean: CheckQRCodeBean
    var mOutType:String=""
    private val reductQRCodeList:java.util.ArrayList<String> by lazy { ArrayList<String>() }
    //==============================================/Field==========================================================
    override fun setInitData(datum: CenterOutSendMxBean,outType:String) {
        mOutType = outType
        mDatum = datum
    }
    override fun enqueueQRCode(scanCodeResult: String?) {
        if (!reductQRCodeList.contains(scanCodeResult)) {
            FeedbackUtils.vibrate(context, 200)
            queueQRCode.add(scanCodeResult)
        } else {
            DialogDirector.showDialog(
                DialogBuilderYesImpl(context),
                "提示",
                "此条码\n $scanCodeResult \n已扫描!"
            )
        }
    }
    //start Async thread to scan QR code.
    //Be sure to prevent this method from being started twice
    override fun startScanQRCodeThread() {
        doAsync {
            whileLoop@ while (!CenterOutSendDetailPresenterImpl.threadExit) {
                /*if (scanToTal >= planTotal) {
                    break@whileLoop
                }*/
                if (queueQRCode.isNotEmpty() && queueQRCode.size > 0) {
                    if (!App.offLineFlag) {
                        checkQRCode(queueQRCode.removeAt(0))
                    } else {
                        //opening thread here in order to simulate checking QRCode offline.
                        doAsync {
                            Thread.sleep(3000)
                            //checkQRCodeOffLine(queueQRCode.removeAt(0))
                        }

                    }
                }
            }
        }
    }
    private fun checkQRCode(QRCode: String) {
        RetrofitUtils.getRetrofit(App.base_url).create(ServiceApi::class.java)
            .checkQRCode(
                "18",
                mOutType,
                QRCode,
                mDatum.出库单位,
                mDatum.入库单位,
                mDatum.单号
            ).enqueue(object : Callback<CheckQRCodeBean> {
                override fun onFailure(call: Call<CheckQRCodeBean>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            centerOutSendReduceBoxView.onError(t.toString())
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CheckQRCodeBean>,
                    response: Response<CheckQRCodeBean>
                ) {
                    checkQRCodeBean = response.body() as CheckQRCodeBean
                    reductQRCodeList.add(QRCode)
                    centerOutSendReduceBoxView.updateView(reductQRCodeList.size)
                }

            })
    }
}