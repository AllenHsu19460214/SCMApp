package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.barcode.Scanner
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.DataListAdapter
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterOutSendBean
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.model.bean.ExceptionCodeInfoBean
import com.bjjc.scmapp.model.vo.CenterOutSendDetailVo
import com.bjjc.scmapp.model.vo.CheckScanCodeVo
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.ui.fragment.DataListFragment
import com.bjjc.scmapp.ui.fragment.DetailListFragment
import com.bjjc.scmapp.ui.fragment.ExceptionListFragment
import com.bjjc.scmapp.util.*
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.layout_aty_center_out_send_detail.*
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList

@SuppressLint("CommitTransaction")
class CenterOutSendDetailActivity : BaseActivity(), ToolbarManager, DataListFragment.IOnUpdateCountTotalListener {
    override val context: Context by lazy { this }
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private lateinit var data: CenterOutSendBean
    private lateinit var centerDistributionOrderOutputDetailVo: CenterOutSendDetailVo
    private lateinit var checkScanCodeVo: CheckScanCodeVo
    private lateinit var orderList: List<CenterOutSendDetailBean>
    private var scanToTal: Long = -1
    private var planBoxTotal: Long = 0
    private val handheldScanHandler = HandheldScanHandler()
    private var searchView: SearchView? = null
    private val dataListFragment: DataListFragment = DataListFragment()
    private val detailListFragment: DetailListFragment = DetailListFragment()
    private val exceptionListFragment: ExceptionListFragment = ExceptionListFragment()
    private var currentFragment: Fragment? = null
    private var iOnUpdateScanCountListener: IOnUpdateScanCountListener? = null
    private var exceptionCodeInfoList: ArrayList<ExceptionCodeInfoBean> = ArrayList()
    private var exceptionCodeList: ArrayList<String> = ArrayList()
    private val currentScanCodeList: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()
    private val temp1: ArrayList<String> = arrayListOf()
    private val checkSucceededScanCodeList: ArrayList<String> = arrayListOf()
    private var dialogFlag: Boolean = false
    private var threadExit: Boolean = false

    companion object {
        lateinit var orderListChanged: List<CenterOutSendDetailBean>
    }

    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send_detail

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
                    bundle.putSerializable("data", orderList as Serializable)
                    detailListFragment.arguments = bundle
                    switchFragment(detailListFragment).commit()
                }
                R.id.rbExceptionList -> {
                    switchFragment(exceptionListFragment).commit()
                }
            }

        }
        btnOutputSubmit.setOnClickListener {
            exceptionCodeInfoList.clear()
            setRbExceptionTextColor()
            exceptionListFragment.updateList()
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
        //start Async thread.
        doAsync {
            whileLoop@ while (!threadExit) {
                //info { "1running from Thread:${Thread.currentThread()}" }
                if (scanToTal >= planBoxTotal) {
                    //info { "2running from Thread:${Thread.currentThread()}" }
                    break@whileLoop
                }
                //info { "3running from Thread:${Thread.currentThread()}" }
                if (currentScanCodeList.isNotEmpty() && currentScanCodeList.size > 0) {
                    if (App.offLineFlag) {
                        doAsync {
                            Thread.sleep(3000)
                            checkScanCodeFromServerOffLine(currentScanCodeList.removeAt(0))
                        }
                    } else {
                        checkScanCodeFromServer(currentScanCodeList.removeAt(0))
                    }
                }
            }
        }
        initCenterOutSendDetailToolBar()
        data = intent.getSerializableExtra("WaybillDetail") as CenterOutSendBean
        if (App.offLineFlag) {
            getGoodsOrderDetailOffLine(data.单号, "CK")
        } else {
            getGoodsOrderDetail(data.单号, "CK")
        }
        setRbExceptionTextColor()


    }

    private fun checkScanCodeFromServerOffLine(scanCode: String) {
        var checkCodeResult: String = ""
        val gson = Gson()
        val code020Json = readFileUtils.getFromAssets(this@CenterOutSendDetailActivity, "offline/code-020.json")
        val code530Json = readFileUtils.getFromAssets(this@CenterOutSendDetailActivity, "offline/code-530.json")
        val code071Json = readFileUtils.getFromAssets(this@CenterOutSendDetailActivity, "offline/code-071.json")

        val code020List = gson.fromJson<List<String>>(code020Json, object : TypeToken<List<String>>() {}.type)
        val code530List = gson.fromJson<List<String>>(code530Json, object : TypeToken<List<String>>() {}.type)
        val code071List = gson.fromJson<List<String>>(code071Json, object : TypeToken<List<String>>() {}.type)

        when {
            code020List.contains(scanCode) -> checkCodeResult =
                    readFileUtils.getFromAssets(this@CenterOutSendDetailActivity, "offline/checkScanCode08-020.json")
            code530List.contains(scanCode) -> checkCodeResult =
                    readFileUtils.getFromAssets(this@CenterOutSendDetailActivity, "offline/checkScanCode08-530.json")
            code071List.contains(scanCode) -> checkCodeResult =
                    readFileUtils.getFromAssets(this@CenterOutSendDetailActivity, "offline/checkScanCode071.json")
        }
        runOnUiThread {
            checkScanCodeVo = gson.fromJson<CheckScanCodeVo>(checkCodeResult, CheckScanCodeVo::class.java)
            if (dealScanCodeByResultCode(scanCode)) return@runOnUiThread
            setRbExceptionTextColor()
            updateScanCodeTotal()
            if (scanToTal >= planBoxTotal) {
                ToastUtils.showShortToast(this@CenterOutSendDetailActivity, "已达到计划总量 $planBoxTotal 箱")
            }
        }

    }

    private fun getGoodsOrderDetailOffLine(单号: String, smtype: String) {
        val centerOutSendDetailVoJson = when (单号) {
            "WLD2018111216311209001" -> {
                readFileUtils.getFromAssets(
                    this@CenterOutSendDetailActivity,
                    "offline/goodsOrderDetailWLD2018111216311209001.json"
                )
            }
            "WLD2018120716125910501" -> {
                readFileUtils.getFromAssets(
                    this@CenterOutSendDetailActivity,
                    "offline/goodsOrderDetailWLD2018120716125910501.json"
                )
            }
            "WLD2018121215364010501" -> {
                readFileUtils.getFromAssets(
                    this@CenterOutSendDetailActivity,
                    "offline/goodsOrderDetailWLD2018120716125910501.json"
                )
            }
            "WLD2018121315472310501" -> {
                readFileUtils.getFromAssets(
                    this@CenterOutSendDetailActivity,
                    "offline/goodsOrderDetailWLD2018120716125910501.json"
                )
            }
            "WLD2018121409401210502" -> {
                readFileUtils.getFromAssets(
                    this@CenterOutSendDetailActivity,
                    "offline/goodsOrderDetailWLD2018120716125910501.json"
                )
            }
            "WLD2018121410115610501" -> {
                readFileUtils.getFromAssets(
                    this@CenterOutSendDetailActivity,
                    "offline/goodsOrderDetailWLD2018120716125910501.json"
                )
            }
            "WLD2018121410562810501" -> {
                readFileUtils.getFromAssets(
                    this@CenterOutSendDetailActivity,
                    "offline/goodsOrderDetailWLD2018120716125910501.json"
                )
            }
            "WLD2018121414423010501" -> {
                readFileUtils.getFromAssets(
                    this@CenterOutSendDetailActivity,
                    "offline/goodsOrderDetailWLD2018120716125910501.json"
                )
            }
            else -> {
                readFileUtils.getFromAssets(
                    this@CenterOutSendDetailActivity,
                    "offline/goodsOrderDetailWLD2018111216311209001.json"
                )
            }
        }
        info { centerOutSendDetailVoJson }
        val gson = Gson()
        centerDistributionOrderOutputDetailVo =
                gson.fromJson<CenterOutSendDetailVo>(centerOutSendDetailVoJson, CenterOutSendDetailVo::class.java)
        showOrderDetailList()
    }

    private fun checkScanCodeFromServer(scanCode: String) {
        RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
            .checkScanCode(
                "18",
                "ZXKCK",
                scanCode,
                "广州库(成都日鸿)",
                "无锡市众达汽车销售服务有限公司,无锡市众达汽车销售服务有限公司",
                "WLD2018111216311209001"
            ).enqueue(object : Callback<CheckScanCodeVo> {
                override fun onFailure(call: Call<CheckScanCodeVo>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            myToast(t.toString())
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CheckScanCodeVo>,
                    response: Response<CheckScanCodeVo>
                ) {
                    //myToast(response.body().toString())
                    checkScanCodeVo = response.body() as CheckScanCodeVo
                    temp1.add(scanCode)
                    info {
                        "========================================================================================================"
                    }
                    Log.i("CenterOutSend", temp1.toString())
                    if (dealScanCodeByResultCode(scanCode)) return
                    setRbExceptionTextColor()
                    updateScanCodeTotal()
                    if (scanToTal >= planBoxTotal) {
                        ToastUtils.showShortToast(this@CenterOutSendDetailActivity, "已达到计划总量 $planBoxTotal 箱")
                    }
                }

            })
    }

    private fun dealScanCodeByResultCode(scanCode: String): Boolean {
        when (checkScanCodeVo.code) {
            "08" -> {
                if (checkSucceededScanCodeList.contains(scanCode)) {
                    return true
                }
                checkSucceededScanCodeList.add(scanCode)
                loop@ for ((index, value) in orderListChanged.withIndex()) {
                    if (value.出库箱数 < value.计划箱数) {
                        if (checkScanCodeVo.wlbm == value.物料编码) {
                            orderListChanged[index].出库箱数++
                            iOnUpdateScanCountListener?.OnUpdateScanCount()
                            break@loop
                        }
                    }
                }
            }
            "071" -> {
                //Occurs exception code.
                val exceptionCodeInfoBean = ExceptionCodeInfoBean()
                exceptionCodeInfoBean.code = scanCode
                exceptionCodeInfoBean.msg = checkScanCodeVo.msg
                if (exceptionCodeInfoList.contains(exceptionCodeInfoBean)) {
                    return true
                }
                exceptionCodeList.add(scanCode)
                exceptionCodeInfoList.add(exceptionCodeInfoBean)
                val bundle = Bundle()
                bundle.putSerializable("exceptionCodeInfoList", exceptionCodeInfoList as Serializable)
                exceptionListFragment.arguments = bundle
                exceptionListFragment.updateList()
                ToastUtils.showLongToast(
                    this@CenterOutSendDetailActivity,
                    exceptionCodeInfoList.toString()
                )

                if (!dialogFlag) {
                    dialogFlag = true
                    DialogUtils.instance()
                        .customDialogYes(this@CenterOutSendDetailActivity)
                        .setTitle("提示")
                        .setMessage(
                            "异常箱码: \n$scanCode\n" +
                                    "异常信息: \n${checkScanCodeVo.msg}"
                        )
                        .setOnPositiveClickListener(object : DialogUtils.OnPositiveClickListener {
                            override fun onPositiveBtnClicked() {
                                dialogFlag = false
                            }
                        })
                        .show()
                }


            }

        }
        return false
    }

    /**
     * Sets text color of RadioButtonException.
     */
    private fun setRbExceptionTextColor() {
        if (exceptionCodeInfoList.size > 0) {
            rbExceptionList.textColor = resources.getColor(R.color.red)
        } else {
            rbExceptionList.setTextColor(resources.getColorStateList(R.color.selector_radio_btn_textcolor))
        }
    }

    /**
     * Gets Detail of orders for goods from logistics Documents.
     */
    private fun getGoodsOrderDetail(orderNumber: String, smtype: String) {
        val progressDialog =
            ProgressDialogUtils.showProgressDialog(this@CenterOutSendDetailActivity, "正在获取数据中!")
        RetrofitUtils.getRetrofit(App.base_url!!).create(ServiceApi::class.java)
            .centerOutSend(
                "3",
                App.loginVo!!.key,
                orderNumber,
                smtype
            ).enqueue(object : Callback<CenterOutSendDetailVo> {
                override fun onFailure(call: Call<CenterOutSendDetailVo>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()
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
                    showOrderDetailList()
                }

            })
    }

    private fun showOrderDetailList() {
        if (centerDistributionOrderOutputDetailVo.code == "10") {
            myToast(centerDistributionOrderOutputDetailVo.msg)
            orderList = centerDistributionOrderOutputDetailVo.mx
            //Sets order number.
            tvOrderNumber.text = centerDistributionOrderOutputDetailVo.单号
            //Deals data
            orderListChanged = orderList

            //===========================TestDataStart===========================================================================
            for ((indexTest, valueTest) in orderListChanged.withIndex()) {
                when (valueTest.原始订单号) {
                    "2906261286-2-4" -> {
                        orderListChanged[indexTest].出库箱数 += 25
                        //orderListChanged[indexTest].允许输入箱数+=100
                    }
                    "2906261800-1-1" -> {
                        orderListChanged[indexTest].出库箱数 += 10
                        //orderListChanged[indexTest].允许输入箱数+=101
                    }
                    "2906261800-1-2" -> {
                        orderListChanged[indexTest].出库箱数 += 5
                        //orderListChanged[indexTest].允许输入箱数+=102
                    }
                    "2906261800-2-1" -> {
                        orderListChanged[indexTest].出库箱数 += 15
                        //orderListChanged[indexTest].允许输入箱数+=103
                    }
                    "FOCEJS059201810250002-1" -> {
                        orderListChanged[indexTest].出库箱数 += 20
                    }
                    "Z3B054M2018111242" -> {
                        orderListChanged[indexTest].出库箱数 += 25
                    }
                }
            }
            //===========================TestDataEnd==========================================================================
            //Sets whether noCodeCount input is allowed
            //1-扫码，0-输数
            if (centerDistributionOrderOutputDetailVo.仓是否输入 == 0) {
                for ((indexTest, valueTest) in orderListChanged.withIndex()) {
                    orderListChanged[indexTest].是否允许扫描 = 0
                }
            }
            //Sets planning box count
            for (mx: CenterOutSendDetailBean in orderListChanged) {
                planBoxTotal += mx.计划箱数
            }
            tvPlanBoxTotal.text = planBoxTotal.toString()
            //Transmit data from CenterOutSendDetailActivity to DataListFragment.
            val bundle = Bundle()
            bundle.putSerializable("LogisticsDocumentsDetail", orderListChanged as Serializable)
            dataListFragment.arguments = bundle
            //Setting dataList is initially selected.
            rbExceptionList.isChecked = true
            rbDataList.isChecked = true
            updateScanCodeTotal()
        } else {
            myToast(centerDistributionOrderOutputDetailVo.msg)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        searchView = setToolBarMenu(false)
        val menuItem = menu?.findItem(R.id.setting)
        menuItem?.isVisible = false
        return true
    }

    @SuppressLint("HandlerLeak")
    private inner class HandheldScanHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Scanner.BARCODE_READ -> {
                    //Display the bar code read
                    val currentScanCode: String = msg.obj.toString()
                    ToastUtils.showShortToast(this@CenterOutSendDetailActivity, currentScanCode)
                    if (scanToTal >= planBoxTotal) {
                        FeedbackUtils.vibrate(
                            this@CenterOutSendDetailActivity,
                            longArrayOf(100, 100, 100, 100, 100, 100, 100, 100),
                            false
                        )
                        ToastUtils.showShortToast(this@CenterOutSendDetailActivity, "已达到计划总量 $planBoxTotal 箱")
                        return
                    }
                    if (!checkSucceededScanCodeList.contains(currentScanCode) && !exceptionCodeList.contains(
                            currentScanCode
                        )
                    ) {
                        FeedbackUtils.vibrate(this@CenterOutSendDetailActivity, 200)
                        currentScanCodeList.add(currentScanCode)
                        //readScanCodeList.add(currentScanCode)
                    } else {
                        DialogUtils.instance()
                            .customDialogYes(this@CenterOutSendDetailActivity)
                            .setTitle("提示")
                            .setMessage("此条码\n $currentScanCode \n已扫描!")
                            .show()
                    }

                }
                Scanner.BARCODE_NOREAD -> {
                }
            }
        }
    }

    /**
     * Updates count of ScanCode and show it.
     */
    private fun updateScanCodeTotal() {
        scanToTal = 0
        for (mx in orderListChanged) {
            scanToTal += mx.出库箱数
        }
        tvScanTotal.text = scanToTal.toString()
    }

    override fun onStart() {
        Scanner.m_handler = handheldScanHandler
        //Initializes scanner of handheld.
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

    override fun onDestroy() {
        threadExit = true
        super.onDestroy()
    }
}
