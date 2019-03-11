package com.bjjc.scmapp.presenter.impl

import android.content.Context
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterOutSendBean
import com.bjjc.scmapp.model.bean.CommonResultBean
import com.bjjc.scmapp.model.vo.CenterOutSendDetailVo
import com.bjjc.scmapp.presenter.interf.CenterOutSendDetailPresenter
import com.bjjc.scmapp.util.ProgressDialogUtils
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import com.bjjc.scmapp.util.readFileUtils
import com.bjjc.scmapp.view.CenterOutSendDetailView
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Allen on 2019/01/09 9:56
 */
class CenterOutSendDetailPresenterImpl(var context:Context,var centerOutSendDetailView: CenterOutSendDetailView):CenterOutSendDetailPresenter {

    override fun loadWaybillDetailData(isRefresh: Boolean,data: CenterOutSendBean) {
        if (App.offLineFlag){
            loadDetailDataOffLine(data)
        }else{
            loadDetailDataFromServer(isRefresh,data)
        }
    }
    /**
     * Gets Detail of orders for goods from waybill.
     */
    private fun loadDetailDataFromServer(refresh: Boolean,data: CenterOutSendBean) {
        val progressDialog =
            ProgressDialogUtils.showProgressDialog(context, "正在获取数据中!")
        RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
            .centerOutSendDetail(
                "3",
                App.loginVo!!.key,
                data.单号,
                "CK"
            ).enqueue(object : Callback<CenterOutSendDetailVo> {
                override fun onFailure(call: Call<CenterOutSendDetailVo>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()
                                centerOutSendDetailView.onError(t.message)
                            }
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CenterOutSendDetailVo>,
                    response: Response<CenterOutSendDetailVo>
                ) {
                    // 判断等待框是否正在显示
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()// 关闭等待框
                    }
                    centerOutSendDetailView.onLoadWaybillDetailDataSuccess(response.body() as CenterOutSendDetailVo)
                }

            })
    }

    /**
     * Offline Data
     */
    private fun loadDetailDataOffLine(data: CenterOutSendBean) {
        val centerOutSendDetailVoJson = when (data.单号) {
            "WLD2018111216311209001" -> {
                readFileUtils.getFromAssets(
                    context,
                    "offline/goodsOrderDetailWLD2018111216311209001.json"
                )
            }
            "WLD2018120716125910501" -> {
                readFileUtils.getFromAssets(
                    context,
                    "offline/goodsOrderDetailWLD2018120716125910501.json"
                )
            }
            "WLD2018121215364010501" -> {
                readFileUtils.getFromAssets(
                    context,
                    "offline/goodsOrderDetailWLD2018121215364010501.json"
                )
            }
            "WLD2018121315472310501" -> {
                readFileUtils.getFromAssets(
                    context,
                    "offline/goodsOrderDetailWLD2018121315472310501.json"
                )
            }
            "WLD2018121409401210502" -> {
                readFileUtils.getFromAssets(
                    context,
                    "offline/goodsOrderDetailWLD2018121409401210502.json"
                )
            }
            "WLD2018121410115610501" -> {
                readFileUtils.getFromAssets(
                    context,
                    "offline/goodsOrderDetailWLD2018121410115610501.json"
                )
            }
            "WLD2018121410562810501" -> {
                readFileUtils.getFromAssets(
                    context,
                    "offline/goodsOrderDetailWLD2018121410562810501.json"
                )
            }
            "WLD2018121414423010501" -> {
                readFileUtils.getFromAssets(
                    context,
                    "offline/goodsOrderDetailWLD2018120716125910501.json"
                )
            }
            else -> {
                readFileUtils.getFromAssets(
                    context,
                    "offline/goodsOrderDetailWLD2018111216311209001.json"
                )
            }
        }
        val gson = Gson()
        val datum: CenterOutSendDetailVo =
            gson.fromJson<CenterOutSendDetailVo>(centerOutSendDetailVoJson, CenterOutSendDetailVo::class.java)
        centerOutSendDetailView.onLoadWaybillDetailDataSuccess(datum)
    }
    //saves order info to server.
    override fun commitOrSaveOrderInfo2Server(
        b: Boolean,
        data: CenterOutSendBean,
        info: String,
        trace: String,
        point: String?
    ) {
        val progressDialog =
            ProgressDialogUtils.showProgressDialog(context, "正在保存数据中!")
        RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
            .centerOutSendDetailSaveOrderInfo(
                "1",
                App.loginVo!!.key,
                data.单号,
                if(b){"已出库"}else{"未出完"},
                info,
                trace,
                point,
                "0",
                "0"
            ).enqueue(object : Callback<CommonResultBean> {
                override fun onFailure(call: Call<CommonResultBean>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()
                                centerOutSendDetailView.onError(t.message)
                            }
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CommonResultBean>,
                    response: Response<CommonResultBean>
                ) {
                    // 判断等待框是否正在显示
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()// 关闭等待框
                    }
                    centerOutSendDetailView.onCommitOrSaveOrderInfoSuccess(response.body() as CommonResultBean)
                }

            })
    }

}