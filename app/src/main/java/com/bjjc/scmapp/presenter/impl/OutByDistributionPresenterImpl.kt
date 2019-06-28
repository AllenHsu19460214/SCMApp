package com.bjjc.scmapp.presenter.impl

import android.content.Context
import android.util.Log
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.OutByDistributeDocInfoBean
import com.bjjc.scmapp.model.bean.OutByDistributionBean
import com.bjjc.scmapp.model.entity.NetEntity
import com.bjjc.scmapp.model.entity.VersionEntity
import com.bjjc.scmapp.presenter.base.OutByDistributionBasePresenter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.ui.view.IView
import com.bjjc.scmapp.util.readFileUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Allen on 2019/01/04 15:35
 */
class OutByDistributionPresenterImpl(var iView: IView) :
    OutByDistributionBasePresenter(), Callback<String> {
    private val TAG = OutByDistributionPresenterImpl::class.java.simpleName
    private var context: Context = iView as BaseActivity

    override fun onFailure(call: Call<String>, t: Throwable) {
        doAsync {
            Thread.sleep(2000)
            uiThread {
                t.message?.let {
                iView.onDataFailure(mapOf("msg" to it))
                }
            }
        }
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        if (response.isSuccessful){
            val result = response.body()
            parseResult(result)
        }

    }
    private fun parseResult(result: String?) {
        val jsonObject = JSONObject(result)
        val code = jsonObject.getString("code")
        val msg = jsonObject.getString("msg")
        if ("10" == code) {
            Log.d(TAG, "$code==>$msg")
            val mx = jsonObject.getString("mx")
            val outByDistributionBeanList: ArrayList<OutByDistributionBean> =
                Gson().fromJson(mx,object :TypeToken< ArrayList<OutByDistributionBean>>() {}.type)
           iView.onDataSuccess(mapOf("msg" to msg,"outByDistributionBeanList" to outByDistributionBeanList))
        } else {
            Log.e(TAG, "$code==>$msg")
            iView.onDataFailure(mapOf("msg" to msg))
        }
    }
    //OutByDistributionBean
    override fun loadData() {
        if(!VersionEntity.isOffline){
            NetEntity.serviceController
                .centerOutSend(
                    url = App.sUriBean?.storeJson!!,
                    command = "7",
                    key = App.sKey,
                    djtype = "WLD",//单据类型标识 THJHD – 提货单   CKJHD – 中心库出库单  YKJHD – 移库单    WLD – 物流单  FXDD – 反向订单
                    crktype = "CK",//要返回的出入库列表表识 CK – 待出库的列表 RK – 待入库的列表
                    sysIndex = "0"
                ).enqueue(this)
        }else{
            loadDataFromLocal()
        }

    }
    //====================================================OffLineData====================================================================
    private fun loadDataFromLocal() {
        val centerOutSendBeanJson = readFileUtils.getFromAssets(context, "offline/logisticsDocuments.json")
        val centerOutSendBean = Gson().fromJson<OutByDistributeDocInfoBean>(centerOutSendBeanJson, OutByDistributeDocInfoBean::class.java)
        //iView.onDataSuccess(centerOutSendBean.mx)
    }
    //====================================================/OffLineData===================================================================
}