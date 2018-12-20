package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.barcode.Scanner
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.DataListAdapter
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterOutSendBean
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.model.vo.CenterOutSendDetailVo
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.ui.fragment.DataListFragment
import com.bjjc.scmapp.ui.fragment.DetailListFragment
import com.bjjc.scmapp.ui.fragment.ExceptionListFragment
import com.bjjc.scmapp.util.ProgressDialogUtils
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.ToolbarManager
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import kotlinx.android.synthetic.main.activity_center_distribution_order_output_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

@SuppressLint("CommitTransaction")
class CenterOutSendDetailActivity : BaseActivity(), ToolbarManager,
    DataListFragment.IOnUpdateCountTotalListener {

    override val context: Context by lazy { this }
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private lateinit var data: CenterOutSendBean
    private lateinit var centerDistributionOrderOutputDetailVo: CenterOutSendDetailVo
    private lateinit var mxList: List<CenterOutSendDetailBean>
    private var scanToTal:Long=0
    private var planBoxTotal: Long = 0
    private val mHandler = MainHandler()
    private var searchView: SearchView? = null
    private val dataListFragment: DataListFragment by lazy { DataListFragment() }
    private val detailListFragment: DetailListFragment by lazy { DetailListFragment() }
    private val exceptionListFragment: ExceptionListFragment by lazy { ExceptionListFragment() }
    private var currentFragment: Fragment? = null
    private var iOnUpdateScanCountListener: IOnUpdateScanCountListener? = null
    private  var exceptionCodeList:ArrayList<String> =ArrayList()

    companion object {
        lateinit var mxListChanged: List<CenterOutSendDetailBean>
    }

    override fun getLayoutId(): Int = R.layout.activity_center_distribution_order_output_detail

    override fun initView() {
        tvNoCodeTotal.text = DataListAdapter.noCodeCount
    }

    //Shows fragment by toggle display.
    private fun switchFragment(targetFragment: Fragment): FragmentTransaction {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (!targetFragment.isAdded) {
            if (currentFragment != null) {
                transaction.hide(currentFragment!!)
            }
            transaction.add(R.id.flFragmentContent, targetFragment, targetFragment::class.java.name)
        } else {
            transaction
                .hide(currentFragment!!)
                .show(targetFragment)
        }
        currentFragment = targetFragment
        return transaction
    }

    override fun initListener() {
        rgListButton.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbDataList -> {
                    switchFragment(dataListFragment).commit()
                }
                R.id.rbDetailList -> {
                    val bundle = Bundle()
                    bundle.putSerializable("data", mxList as Serializable)
                    detailListFragment.arguments = bundle
                    switchFragment(detailListFragment).commit()
                }
                R.id.rbExceptionList -> {
                    switchFragment(exceptionListFragment).commit()
                }
            }

        }
        btnOutputSubmit.setOnClickListener {
            exceptionCodeList.clear()
            if (exceptionCodeList.size>0){
                rbExceptionList.textColor=resources.getColor(R.color.red)
            }else{
                rbExceptionList.setTextColor(resources.getColorStateList(R.color.selector_radio_btn_textcolor))
            }
        }
        dataListFragment.setOnUpdateCountTotalListener(this)
    }

    override fun onUpdateCountTotal(mData: ArrayList<HashMap<String, String>>?) {
        var total = 0
        mData?.forEach {
            if ("" != it["list_item_inputValue"]) {
                total += it["list_item_inputValue"]?.toInt()!!
            }
        }
        tvNoCodeTotal.text = total.toString()
    }

    override fun initData() {
        initCenterDistributionOrderOutputDetailToolBar()
        data = intent.getSerializableExtra("mingXi") as CenterOutSendBean
        getOrderDetail(data.单号, "CK")
        if (exceptionCodeList.size>0){
            rbExceptionList.textColor=resources.getColor(R.color.red)
        }else{
            rbExceptionList.setTextColor(resources.getColorStateList(R.color.selector_radio_btn_textcolor))
        }
    }

    private fun getOrderDetail(orderNumber: String, smtype: String) {
        val progressDialog =
            ProgressDialogUtils.showProgressDialog(this@CenterOutSendDetailActivity, "正在获取数据中!")
        RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
            .centerDistributionOrderOutput(
                "3",
                App.loginVo!!.key,
                orderNumber,
                smtype
            ).enqueue(object : Callback<CenterOutSendDetailVo> {
                override fun onFailure(call: Call<CenterOutSendDetailVo>, t: Throwable) {
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
                    call: Call<CenterOutSendDetailVo>,
                    response: Response<CenterOutSendDetailVo>
                ) {
                    // 判断等待框是否正在显示
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()// 关闭等待框
                    }
                    //myToast(response.body().toString())
                    centerDistributionOrderOutputDetailVo = response.body() as CenterOutSendDetailVo
                    //info { loginVo }
                    if (centerDistributionOrderOutputDetailVo.code == "10") {
                        myToast(centerDistributionOrderOutputDetailVo.msg)
                        mxList = centerDistributionOrderOutputDetailVo.mx
                        //Sets order number.
                        tvOrderNumber.text = centerDistributionOrderOutputDetailVo.单号
                        //Deals data
                        mxListChanged = mxList
                        //===========================测试数据start===========================================================================
                        for((indexTest,valueTest) in mxListChanged.withIndex()){
                            when(valueTest.原始订单号){
                                "2906261286-2-4"->{
                                    mxListChanged[indexTest].出库箱数+=25
                                    //mxListChanged[indexTest].允许输入箱数+=100
                                }
                                "2906261800-1-1"->{
                                    mxListChanged[indexTest].出库箱数+=10
                                    //mxListChanged[indexTest].允许输入箱数+=101
                                }
                                "2906261800-1-2"->{
                                    mxListChanged[indexTest].出库箱数+=5
                                    //mxListChanged[indexTest].允许输入箱数+=102
                                }
                                "2906261800-2-1"->{
                                    mxListChanged[indexTest].出库箱数+=15
                                    //mxListChanged[indexTest].允许输入箱数+=103
                                }
                            }
                        }
                        //===========================测试数据end==========================================================================
                        //Sets whether noCodeCount input is allowed
                        //1-扫码，0-输数
                        if (centerDistributionOrderOutputDetailVo.仓是否输入==0){
                            for ( (indexTest,valueTest) in mxListChanged.withIndex()) {
                                mxListChanged[indexTest].是否允许扫描=0
                            }
                        }
                        //Sets planning box count
                        for (mx: CenterOutSendDetailBean in mxListChanged) {
                            planBoxTotal += mx.计划箱数
                        }
                        tvPlanBoxTotal.text = planBoxTotal.toString()
                        //Transmit data from CenterOutSendDetailActivity to DataListFragment.
                        val bundle = Bundle()
                        bundle.putSerializable("data", mxListChanged as Serializable)
                        dataListFragment.arguments = bundle
                        //Setting dataList is initially selected.
                        rbDataList.isChecked = true
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

    var i: Int = 0

    @SuppressLint("HandlerLeak")
    private inner class MainHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Scanner.BARCODE_READ -> {
                    //显示读到的条码
                    val mediaPlayer = MediaPlayer.create(context, R.raw.beep)
                    //mediaPlayer.start()
                    ToastUtils.showShortToast(this@CenterOutSendDetailActivity, msg.obj.toString())

                    if (scanToTal>=planBoxTotal){
                        ToastUtils.showShortToast(this@CenterOutSendDetailActivity,"已达到计划总量 $planBoxTotal 箱")
                        return
                    }
                    loop@ for ((index, value) in mxListChanged.withIndex()) {

                        if (value.出库箱数 < value.计划箱数) {
                            if (msg.obj.toString() == value.备件编号) {
                                mxListChanged[index].出库箱数++
                                break@loop
                            }else{
                                if(!exceptionCodeList.contains(msg.obj.toString())){
                                    exceptionCodeList.add(msg.obj.toString())
                                    ToastUtils.showLongToast(this@CenterOutSendDetailActivity,exceptionCodeList.toString())
                                }
                            }
                        }

                    }
                    if (exceptionCodeList.size>0){
                        rbExceptionList.textColor=resources.getColor(R.color.red)
                    }else{
                        rbExceptionList.setTextColor(resources.getColorStateList(R.color.selector_radio_btn_textcolor))
                    }
                    scanToTal=0
                    for(mx in mxListChanged){
                        scanToTal+=mx.出库箱数
                    }
                    tvScanTotal.text=scanToTal.toString()
                    if (scanToTal>=planBoxTotal){
                        ToastUtils.showShortToast(this@CenterOutSendDetailActivity,"已达到计划总量 $planBoxTotal 箱")
                    }
                    iOnUpdateScanCountListener?.OnUpdateScanCount()
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

    fun setIOnUpdateScanCountListener(iOnUpdateScanCountListener: IOnUpdateScanCountListener) {
        this.iOnUpdateScanCountListener = iOnUpdateScanCountListener
    }

    interface IOnUpdateScanCountListener {
        fun OnUpdateScanCount()
    }
}
