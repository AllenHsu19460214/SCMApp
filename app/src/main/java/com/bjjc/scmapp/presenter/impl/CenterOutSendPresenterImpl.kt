package com.bjjc.scmapp.presenter.impl

import android.content.Context
import android.util.Log
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.vo.CenterOutSendVo
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
class CenterOutSendPresenterImpl(var context: Context,var centerOutSendView: CenterOutSendView) :CenterOutSendPresenter{

    override fun loadWaybillData() {
        if (App.offLineFlag){
            loadDataOffLine()
        }else{
            loadDataFromServer()
        }
    }
    private fun loadDataFromServer(){
        val progressDialog =
            ProgressDialogUtils.showProgressDialog(context, "数据正在加载中!")
        RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
            .centerOutSend(
                "7",
                App.loginVo?.key,
                "WLD",//单据类型标识 THJHD – 提货单   CKJHD – 中心库出库单  YKJHD – 移库单    WLD – 物流单  FXDD – 反向订单
                "CK",//要返回的出入库列表表识 CK – 待出库的列表 RK – 待入库的列表
                "0"
            ).enqueue(object : Callback<CenterOutSendVo> {
                override fun onFailure(call: Call<CenterOutSendVo>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            // 判断等待框是否正在显示
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()// 关闭等待框
                                centerOutSendView.onError(t.message)
                            }
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CenterOutSendVo>,
                    response: Response<CenterOutSendVo>
                ) {
                    // 判断等待框是否正在显示
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()// 关闭等待框
                    }
                    //myToast(response.body().toString())
                    val centerOutSendVo = response.body() as CenterOutSendVo
                    /*info { centerDistributionOrderOutputVo}
                    info{ mingXi}*/
                    if (centerOutSendVo.code == "10") {
                        centerOutSendView.loadWaybillDataSuccess(centerOutSendVo.mx)
                    } else {
                        centerOutSendView.onError(centerOutSendVo.msg)
                    }
                }

            })
    }
    private fun loadDataOffLine() {
        val centerOutSendVoJson = readFileUtils.getFromAssets(context, "offline/logisticsDocuments.json")
        Log.d("CenterOutSendActivity",centerOutSendVoJson)
        val gson = Gson()
        val centerOutSendVo=gson.fromJson<CenterOutSendVo>(centerOutSendVoJson,CenterOutSendVo::class.java)
        Log.d("CenterOutSendActivity",centerOutSendVo.toString())
        centerOutSendView.loadWaybillDataSuccess(centerOutSendVo.mx)
    }

}