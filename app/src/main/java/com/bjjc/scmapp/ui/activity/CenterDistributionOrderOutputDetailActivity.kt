package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.barcode.Scanner
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.DataListAdapter
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterDistributionOrderOutputMingXiBean
import com.bjjc.scmapp.model.bean.CenterDistributionOrderOutputMingXiDetailBean
import com.bjjc.scmapp.model.vo.CenterDistributionOrderOutputDetailVo
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ProgressDialogUtils
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.ToolbarManager
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import kotlinx.android.synthetic.main.activity_center_distribution_order_output_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CenterDistributionOrderOutputDetailActivity : BaseActivity(),ToolbarManager {
    override val context: Context by lazy { this }
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private lateinit var data:CenterDistributionOrderOutputMingXiBean
    private  var planBoxCount:Long = 0
    private val mHandler = MainHandler()
    private  var searchView: SearchView?=null
    private val dataListAdapter:DataListAdapter by lazy { DataListAdapter(this) }
    override fun getLayoutId(): Int = R.layout.activity_center_distribution_order_output_detail
    override fun initView() {
        rbDataList.isChecked=true
    }
    override fun initData() {
        initCenterDistributionOrderOutputDetailToolBar()
        data = intent.getSerializableExtra("mingXi") as CenterDistributionOrderOutputMingXiBean
        getOrderDetail(data.单号,"CK")

    }

    private fun getOrderDetail(orderNumber:String,smtype:String) {
        val progressDialog = ProgressDialogUtils.showProgressDialog(this@CenterDistributionOrderOutputDetailActivity, "正在获取数据中!")
        RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
            .centerDistributionOrderOutput(
                "3",
                App.loginVo!!.key,
                orderNumber,
                smtype
            ).enqueue(object : Callback<CenterDistributionOrderOutputDetailVo> {
                override fun onFailure(call: Call<CenterDistributionOrderOutputDetailVo>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            // 判断等待框是否正在显示
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()// 关闭等待框
                                myToast(t.toString())
                            }
                        }
                    }
                }

                override fun onResponse(call: Call<CenterDistributionOrderOutputDetailVo>, response: Response<CenterDistributionOrderOutputDetailVo>) {
                    // 判断等待框是否正在显示
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()// 关闭等待框
                    }
                    //myToast(response.body().toString())
                    val centerDistributionOrderOutputDetailVo = response.body() as CenterDistributionOrderOutputDetailVo
                    //info { loginVo }
                    if (centerDistributionOrderOutputDetailVo.code == "10") {
                        myToast(centerDistributionOrderOutputDetailVo.msg)
                        val mxList = centerDistributionOrderOutputDetailVo.mx
                        tvOrderNumber.text=centerDistributionOrderOutputDetailVo.单号
                        for (mx:CenterDistributionOrderOutputMingXiDetailBean in mxList){
                            planBoxCount+=mx.计划箱数
                        }
                        tvPlanBoxCount.text=planBoxCount.toString()
                        dataListAdapter.setData(mxList)
                        lvTest.adapter= dataListAdapter
                    } else {
                        myToast(centerDistributionOrderOutputDetailVo.msg)
                    }
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        searchView = setToolBarMenu(true)
        val menuItem = menu?.findItem(R.id.setting)
        menuItem?.isVisible = false
        return true
    }
    var i:Int=0
    @SuppressLint("HandlerLeak")
    private inner class MainHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Scanner.BARCODE_READ -> {
                    //显示读到的条码
                    val mediaPlayer = MediaPlayer.create(context, R.raw.beep)
                    mediaPlayer.start()
                    ToastUtils.showShortToast(this@CenterDistributionOrderOutputDetailActivity, msg.obj.toString())
                    i++
                    tvScanCount.text=i.toString()
                }
                Scanner.BARCODE_NOREAD -> {
                }

            }
        }
    }
    override fun onStart() {
        //赋值handle句柄
        Scanner.m_handler = mHandler
        //初始化扫描头
        Scanner.InitSCA()
        super.onStart()
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
