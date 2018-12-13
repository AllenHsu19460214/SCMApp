package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.barcode.Scanner
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.CenterDistributionOrderOutputListViewAdapter
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterDistributionOrderOutputMingXiBean
import com.bjjc.scmapp.model.vo.CenterDistributionOrderOutputVo
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.ProgressDialogUtils
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.ToolbarManager
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import kotlinx.android.synthetic.main.activity_center_distribution_order_output.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *  CenterDistributionOrderOutputActivity
 */
class CenterDistributionOrderOutputActivity : BaseActivity(), ToolbarManager {
    companion object {
        var scanNumber:String?=null
    }
    override val context: Context by lazy { this }
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private var data: List<CenterDistributionOrderOutputMingXiBean> = ArrayList()
    private val mHandler = MainHandler()
    private  var searchView:SearchView?=null
    private val centerDistributionOrderOutputListViewAdapter: CenterDistributionOrderOutputListViewAdapter by lazy {
        CenterDistributionOrderOutputListViewAdapter(this)
    }
    override fun getLayoutId(): Int = R.layout.activity_center_distribution_order_output
    override fun initData() {
        initCenterDistributionOrderOutputToolBar()
        postOrderMingXi()
    }

    /**
     * Obtains MintXi of CenterDistributionOrderOutput.
     */
    private fun postOrderMingXi() {
        val progressDialog =
            ProgressDialogUtils.showProgressDialog(this@CenterDistributionOrderOutputActivity, "数据正在加载中!")
        RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
            .centerDistributionOrderOutput(
                "7",
                App.loginVo?.key,
                "WLD",//单据类型标识 THJHD – 提货单   CKJHD – 中心库出库单  YKJHD – 移库单    WLD – 物流单  FXDD – 反向订单
                "CK",//要返回的出入库列表表识 CK – 待出库的列表 RK – 待入库的列表
                "0"
            ).enqueue(object : Callback<CenterDistributionOrderOutputVo> {
                override fun onFailure(call: Call<CenterDistributionOrderOutputVo>, t: Throwable) {
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

                override fun onResponse(
                    call: Call<CenterDistributionOrderOutputVo>,
                    response: Response<CenterDistributionOrderOutputVo>
                ) {
                    // 判断等待框是否正在显示
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()// 关闭等待框
                    }
                    //myToast(response.body().toString())
                    val centerDistributionOrderOutputVo = response.body() as CenterDistributionOrderOutputVo
                    /*info { centerDistributionOrderOutputVo}
                    info{ mingXi}*/
                    if (centerDistributionOrderOutputVo.code == "10") {
                        data = centerDistributionOrderOutputVo.mx
                        centerDistributionOrderOutputListViewAdapter.setData(data)
                        lvCenterDistributionOrderOutput.adapter = centerDistributionOrderOutputListViewAdapter
                        lvCenterDistributionOrderOutput.setOnItemClickListener { parent, view, position, id ->
                            val intent = Intent(
                                this@CenterDistributionOrderOutputActivity,
                                CenterDistributionOrderOutputDetailActivity::class.java
                            )
                            intent.putExtra("mingXi", data[position])
                            startActivity(intent)
                        }
                    } else {
                        myToast(centerDistributionOrderOutputVo.msg)
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

    @SuppressLint("HandlerLeak")
    private inner class MainHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Scanner.BARCODE_READ -> {
                    //显示读到的条码
                    val mediaPlayer = MediaPlayer.create(context, R.raw.beep)
                    mediaPlayer.start()
                    ToastUtils.showShortToast(this@CenterDistributionOrderOutputActivity, msg.obj.toString())
                    scanNumber=msg.obj.toString()
                    searchView?.setQuery( msg.obj.toString(),false)
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
