package com.bjjc.scmapp.presenter.impl

import android.app.ProgressDialog
import android.content.Context
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterOutSendBean
import com.bjjc.scmapp.presenter.interf.CenterOutSendPresenter
import com.bjjc.scmapp.util.ProgressDialogUtils
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import com.bjjc.scmapp.util.readFileUtils
import com.bjjc.scmapp.view.CenterOutSendView
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Allen on 2019/01/04 15:35
 */
class CenterOutSendPresenterImpl(var context: Context, var centerOutSendView: CenterOutSendView) : CenterOutSendPresenter {
    private var progressDialog: ProgressDialog? =null
    override fun onLoad() {
        if (!App.offLineFlag) loadData() else loadOffLineData()
    }

    private fun loadData() {
        progressDialog = ProgressDialogUtils.showProgressDialog(context, "数据正在加载中!")
        RetrofitUtils.getRetrofit(App.base_url).create(ServiceApi::class.java)
            .centerOutSend(
                "7",
                App.loginBean.key,
                "WLD",//单据类型标识 THJHD – 提货单   CKJHD – 中心库出库单  YKJHD – 移库单    WLD – 物流单  FXDD – 反向订单
                "CK",//要返回的出入库列表表识 CK – 待出库的列表 RK – 待入库的列表
                "0"
            ).enqueue(object : Callback<CenterOutSendBean> {
                override fun onFailure(call: Call<CenterOutSendBean>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            // Determines whether the wait box is being displayed
                            progressDialog?.let { progressDialog ->
                                if (progressDialog.isShowing) {
                                    // Closes the waiting dialog.
                                    progressDialog.dismiss()
                                }
                            }
                            centerOutSendView.onError(t.message)
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CenterOutSendBean>,
                    response: Response<CenterOutSendBean>
                ) {
                    // Determines whether the wait box is being displayed
                    progressDialog?.let { progressDialog ->
                        if (progressDialog.isShowing) {
                            // Closes the waiting dialog.
                            progressDialog.dismiss()
                        }
                    }
                    val centerOutSendVo = response.body() as CenterOutSendBean
                    if (centerOutSendVo.code == "10") {
                        centerOutSendView.onSuccess(centerOutSendVo.mx)
                    } else {
                        centerOutSendView.onError(centerOutSendVo.msg)
                    }
                }

            })
    }

    private fun loadOffLineData() {
        val centerOutSendBeanJson = readFileUtils.getFromAssets(context, "offline/logisticsDocuments.json")
        val centerOutSendBean = Gson().fromJson<CenterOutSendBean>(centerOutSendBeanJson, CenterOutSendBean::class.java)
        centerOutSendView.onSuccess(centerOutSendBean.mx)
    }

}