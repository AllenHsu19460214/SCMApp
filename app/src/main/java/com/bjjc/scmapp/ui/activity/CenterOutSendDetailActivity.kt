package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.DataListAdapter
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.common.IntentKey
import com.bjjc.scmapp.model.bean.CenterOutSendBean
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.model.bean.CommonResultBean
import com.bjjc.scmapp.model.bean.ExceptionCodeInfoBean
import com.bjjc.scmapp.model.vo.CenterOutSendDetailVo
import com.bjjc.scmapp.model.vo.CheckScanCodeVo
import com.bjjc.scmapp.presenter.impl.CenterOutSendDetailPresenterImpl
import com.bjjc.scmapp.presenter.interf.CenterOutSendDetailPresenter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.ui.fragment.DataListFragment
import com.bjjc.scmapp.ui.fragment.ExceptionListFragment
import com.bjjc.scmapp.util.*
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import com.bjjc.scmapp.view.CenterOutSendDetailView
import com.common.zxing.CaptureActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.layout_aty_center_out_send_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList

@SuppressLint("CommitTransaction")
class CenterOutSendDetailActivity : BaseActivity(), ToolbarManager, CenterOutSendDetailView, View.OnClickListener {
    //==============================================FieldStart=====================================================================
    override val context: Context by lazy { this }
    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private lateinit var data: CenterOutSendBean
    private lateinit var checkScanCodeVo: CheckScanCodeVo
    private val orderDatas: MutableList<CenterOutSendDetailBean> by lazy { ArrayList<CenterOutSendDetailBean>() }
    private var scanToTal: Long = -1
    private var onCodeToTal: Long = -1
    private var planBoxTotal: Long = 0
    private val handheldScanHandler = HandheldScanHandler()
    private var searchView: SearchView? = null
    private val dataListFragment: DataListFragment by lazy { DataListFragment() }
    private val exceptionListFragment: ExceptionListFragment = ExceptionListFragment()
    private var currentFragment: Fragment? = null
    private var exceptionCodeInfoList: MutableList<ExceptionCodeInfoBean> = ArrayList()
    private var checkScannedExceptionCodeList: MutableList<String> = ArrayList()
    private val currentScanCodeList: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()
    private val temp1: MutableList<String> = arrayListOf()
    private val checkScannedSucceededScanCodeList: MutableList<String> = ArrayList()
    private var dialogFlag: Boolean = false
    private var threadExit: Boolean = false
    private lateinit var toolbarMenu: Toolbar
    private val centerOutSendDetailPresenter: CenterOutSendDetailPresenter by lazy {
        CenterOutSendDetailPresenterImpl(
            this,
            this
        )
    }
    private val orderDataChanged: MutableList<CenterOutSendDetailBean> by lazy { ArrayList<CenterOutSendDetailBean>() }
    private var isWipedCache: Boolean = false
    //==============================================FieldEnd=====================================================================
    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send_detail

    override fun initView() {
        if (!App.isPDA) {
            btnScan_CenterOutSendDetailActivity.visibility = View.VISIBLE
        }
        tvNoCodeTotal.text = DataListAdapter.noCodeCount
        onCodeToTal = DataListAdapter.noCodeCount.toLong()
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
                /*R.id.rbDetailList -> {
                    val bundle = Bundle()
                    bundle.putSerializable("orderDatas", orderDatas as Serializable)
                    detailListFragment.arguments = bundle
                    switchFragment(detailListFragment).commit()
                }*/
                R.id.rbExceptionList -> {
                    switchFragment(exceptionListFragment).commit()
                }
            }

        }
        btnOutputSubmit.setOnClickListener {
            /*exceptionCodeInfoList.clear()
            setRbtnExceptionTextColor()
            exceptionListFragment.updateList()*/
            // Showing dialog of finished or unfinished to be used to commit or save info of OutOrder.
            for (value in orderDataChanged) {
                value.数量 = value.出库箱数
                value.输入箱数 = value.出库输入箱数
            }
            if (isFinished()) {
                DialogUtils.instance()
                    .customDialogYesOrNo(this@CenterOutSendDetailActivity)
                    .setTitle("提示")
                    .setMessage("订单已完成，是否提交!")
                    .setOnPositiveClickListener(object : DialogUtils.OnPositiveClickListener {
                        override fun onPositiveBtnClicked() {
                            commitOrSaveOutInfo(isFinished())
                        }
                    })
                    .setOnNegativeClickListener(object : DialogUtils.OnNegativeClickListener {
                        override fun onNegativeBtnClicked() {
                        }
                    })
                    .show()
            } else {
                DialogUtils.instance()
                    .customDialogYesOrNo(this@CenterOutSendDetailActivity)
                    .setTitle("提示")
                    .setMessage("订单未完成，是否保存!")
                    .setOnPositiveClickListener(object : DialogUtils.OnPositiveClickListener {
                        override fun onPositiveBtnClicked() {
                            commitOrSaveOutInfo(isFinished())
                        }
                    })
                    .setOnNegativeClickListener(object : DialogUtils.OnNegativeClickListener {
                        override fun onNegativeBtnClicked() {
                        }
                    })
                    .show()
            }
        }
        btnScan_CenterOutSendDetailActivity.setOnClickListener(this)
    }

    private fun isFinished(): Boolean {
        if (planBoxTotal == calculateReality()) {
            return true
        }
        return false
    }

    private fun commitOrSaveOutInfo(b: Boolean) {
        val info = getSaveOrderInfoJson(orderDataChanged)
        val trace = getTraceJson()
        //centerOutSendDetailPresenter.commitOrSaveOrderInfo2Server(b,data,info,trace)
    }

    private fun getSaveOrderInfoJson(orderDataChanged: MutableList<CenterOutSendDetailBean>): String {
        val gson = Gson()
        val json = gson.toJson(orderDataChanged)
        return json
    }

    private fun getTraceJson(): String {
        val gson = Gson()
        val trace = gson.toJson(checkScannedSucceededScanCodeList)
        return trace
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnScan_CenterOutSendDetailActivity -> {
                //跳转到扫描页面，扫描条形码或二维码
                val openCameraIntent = Intent(context, CaptureActivity::class.java)
                openCameraIntent.putExtra("autoEnlarged", true)
                startActivityForResult(openCameraIntent, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (0 == requestCode) {
        }
        when (resultCode) {
            RESULT_OK ->
                //1.Returns value of bar code scanned.
            {
                //info{data?.extras?.getString("result")}
                val barCodeValue: String? = data?.extras?.getString("result")
                ToastUtils.showShortToast(this@CenterOutSendDetailActivity, barCodeValue)
                //vibrate Feedback。
                FeedbackUtils.vibrate(this@CenterOutSendDetailActivity, 200)
                addScanCodeNum(barCodeValue)
            }
        }
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

        data =
            intent.getSerializableExtra(IntentKey.CENTER_OUT_SEND_AND_CENTER_OUT_SEND_DETAIL_CURRENTDATA) as CenterOutSendBean
        /**
         * after sp save.
         */

        if (null != SPUtils.getBean(context, "orderDataChanged${data.单号}")
            || null != SPUtils.getBean(context, "checkScannedSucceededScanCodeList${data.单号}")
            || null != SPUtils.getBean(context, "checkScannedExceptionCodeList${data.单号}")
        ) {
            if (null != SPUtils.getBean(context, "checkScannedSucceededScanCodeList${data.单号}")) {
                checkScannedSucceededScanCodeList.clear()
                checkScannedSucceededScanCodeList.addAll(
                    SPUtils.getBean(
                        context,
                        "checkScannedSucceededScanCodeList${data.单号}"
                    ) as MutableList<String>
                )
            }
            if (null != SPUtils.getBean(context, "checkScannedExceptionCodeList${data.单号}")) {
                checkScannedExceptionCodeList.clear()
                checkScannedExceptionCodeList.addAll(
                    SPUtils.getBean(
                        context,
                        "checkScannedExceptionCodeList${data.单号}"
                    ) as MutableList<String>
                )
            }
            if (null != SPUtils.getBean(context, "orderDataChanged${data.单号}")) {
                orderDataChanged.clear()
                orderDataChanged.addAll(
                    SPUtils.getBean(
                        context,
                        "orderDataChanged${data.单号}"
                    ) as MutableList<CenterOutSendDetailBean>
                )
            }
            tvWaybillNumber.text = data.单号
            planBoxTotal = computePlanningBoxNum()
            //Sets planning box count
            tvPlanBoxTotal.text = planBoxTotal.toString()
            //Transmit data from CenterOutSendDetailActivity to DataListFragment.
            transmitData2DataListFragment()
            updateScanCodeTotal()
        } else {
            centerOutSendDetailPresenter.loadWaybillDetailData(false, data)
        }
        setRbtnExceptionTextColor()
    }

    override fun onError(message: String?) {
        message?.let { myToast(it) }
    }

    override fun onLoadWaybillDetailDataSuccess(data: CenterOutSendDetailVo) {
        if (data.code == "10") {
            orderDatas.clear()
            orderDatas.addAll(data.mx)
            orderDataChanged.clear()
            orderDataChanged.addAll(orderDatas)
            //Sets order number.
            tvWaybillNumber.text = data.单号
            setNoCodeIsAllowed(data)
            addTestDataForOrderData(orderDataChanged)
            planBoxTotal = computePlanningBoxNum()
            //Sets planning box count
            tvPlanBoxTotal.text = planBoxTotal.toString()
            updateScanCodeTotal()
            if (isWipedCache) {
                //dataListFragment.dataListAdapter.updateData(orderDataChanged, data.单号)
                dataListFragment.updateData(orderDataChanged)
                dataListFragment.dataListAdapter.notifyDataSetChanged()
                exceptionCodeInfoList.clear()
                setRbtnExceptionTextColor()
                exceptionListFragment.updateList()
                isWipedCache = false
            } else {
                //Transmit data from CenterOutSendDetailActivity to DataListFragment.
                transmitData2DataListFragment()
            }

        } else {
            myToast(data.msg)
        }
    }

    /**
     * Transmit data from CenterOutSendDetailActivity to DataListFragment.
     */
    private fun transmitData2DataListFragment() {
        val bundle = Bundle()
        bundle.putSerializable(
            IntentKey.CENTER_OUT_SEND_AND_DATA_LIST_FRAGMENT_ORDERDATACHANGED,
            orderDataChanged as Serializable
        )
        bundle.putString(
            IntentKey.CENTER_OUT_SEND_AND_DATA_LIST_FRAGMENT_ORDERNUMBER,
            data.单号
        )
        dataListFragment.arguments = bundle

        if (null != SPUtils.getBean(context, "exceptionCodeInfoList${data.单号}")) {
            exceptionCodeInfoList.clear()
            exceptionCodeInfoList.addAll(
                SPUtils.getBean(
                    context,
                    "exceptionCodeInfoList${data.单号}"
                ) as MutableList<ExceptionCodeInfoBean>
            )
            bundle.putSerializable(
                "exceptionCodeInfoList",
                exceptionCodeInfoList as Serializable
            )
            exceptionListFragment.arguments = bundle
        }
        setRbtnExceptionTextColor()
        rbExceptionList.isChecked = true
        //Setting dataList is initially selected.
        rbDataList.isChecked = true
    }

    override fun onSetNoCodeText(noCodeTotal: Long) {
        tvNoCodeTotal.text = noCodeTotal.toString()
        onCodeToTal = noCodeTotal
    }

    override fun onCommitOrSaveOrderInfoSuccess(data: CommonResultBean) {
        if (data.code == "08") {
            //Wiping the cache which correspond to order.
            wipeCacheByWayBillNumber()
            myToast("保存数据成功!")
        } else {
            myToast(data.msg)
        }
    }

    /**
     * Sets whether noCodeCount input is allowed
     * 1-scan QR code，0-input number
     */
    private fun setNoCodeIsAllowed(data: CenterOutSendDetailVo) {
        if (data.仓是否输入 == 0) {
            for (index: Int in 0 until orderDataChanged.size) {
                orderDataChanged[index].是否允许扫描 = 0
            }
        }
    }

    /**
     * Add the test data to order data.
     */
    private fun addTestDataForOrderData(orderDataChanged: List<CenterOutSendDetailBean>) {
        for ((index, valueTest) in orderDataChanged.withIndex()) {
            when (valueTest.原始订单号) {
                "2906261286-2-4" -> {
                    orderDataChanged[index].出库箱数 += 25
                    //orderDataChanged[indexTest].允许输入箱数+=100
                }
                "2906261800-1-1" -> {
                    orderDataChanged[index].出库箱数 += 10
                    //orderDataChanged[indexTest].允许输入箱数+=101
                }
                "2906261800-1-2" -> {
                    orderDataChanged[index].出库箱数 += 5
                    //orderDataChanged[indexTest].允许输入箱数+=102
                }
                "2906261800-2-1" -> {
                    orderDataChanged[index].出库箱数 += 15
                    //orderDataChanged[indexTest].允许输入箱数+=103
                }
                "FOCEJS059201810250002-1" -> {
                    orderDataChanged[index].出库箱数 += 20
                }
                "Z3B054M2018111242" -> {
                    orderDataChanged[index].出库箱数 += 25
                }
            }
        }
    }

    private fun computePlanningBoxNum(): Long {
        var planBoxTotal: Long = 0
        for (mx: CenterOutSendDetailBean in orderDataChanged) {
            planBoxTotal += mx.计划箱数
        }
        return planBoxTotal
    }

    private fun calculateReality(): Long {
        var realityBoxTotal: Long = 0
        for (mx: CenterOutSendDetailBean in orderDataChanged) {
            realityBoxTotal += mx.数量 + mx.输入箱数
        }
        return realityBoxTotal
    }

    private fun checkScanCodeFromServerOffLine(scanCode: String) {
        var checkCodeResult = ""
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
            if (processScanCodeByResultCode(scanCode)) return@runOnUiThread
            setRbtnExceptionTextColor()
            updateScanCodeTotal()
            if (scanToTal >= planBoxTotal) {
                ToastUtils.showShortToast(this@CenterOutSendDetailActivity, "已达到计划总量 $planBoxTotal 箱")
            }
        }

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
                    Log.i("CenterOutSend", temp1.toString())
                    if (processScanCodeByResultCode(scanCode)) return
                    setRbtnExceptionTextColor()
                    updateScanCodeTotal()
                    if (scanToTal >= planBoxTotal) {
                        ToastUtils.showShortToast(this@CenterOutSendDetailActivity, "已达到计划总量 $planBoxTotal 箱")
                    }
                }

            })
    }


    private fun processScanCodeByResultCode(scanCode: String): Boolean {
        when (checkScanCodeVo.code) {
            "08" -> {
                if (checkScannedSucceededScanCodeList.contains(scanCode)) {
                    return true
                }
                loop@ for ((index, value) in orderDataChanged.withIndex()) {
                    if (checkScanCodeVo.wlbm == value.物料编码) {
                        if (value.出库箱数 + value.出库输入箱数 < value.计划箱数) {
                            orderDataChanged[index].出库箱数++
                            checkScannedSucceededScanCodeList.add(scanCode)
                            Thread.sleep(500)
                            dataListFragment.dataListAdapter.notifyDataSetChanged()
                            break@loop
                        } else {
                            FeedbackUtils.vibrate(
                                this@CenterOutSendDetailActivity,
                                longArrayOf(100, 100, 100, 100, 100, 100, 100, 100),
                                false
                            )
                            myToast("该订单:${value.原始订单号}已到达计划箱数!")
                        }
                    }
                }
                SPUtils.putBean(context, "orderDataChanged${data.单号}", orderDataChanged)
                SPUtils.putBean(
                    context,
                    "checkScannedSucceededScanCodeList${data.单号}",
                    checkScannedSucceededScanCodeList
                )
            }
            "071" -> {
                //Occurs exception code.
                val exceptionCodeInfoBean = ExceptionCodeInfoBean()
                exceptionCodeInfoBean.code = scanCode
                exceptionCodeInfoBean.msg = checkScanCodeVo.msg
                if (exceptionCodeInfoList.contains(exceptionCodeInfoBean)) {
                    return true
                }
                checkScannedExceptionCodeList.add(scanCode)
                exceptionCodeInfoList.add(exceptionCodeInfoBean)
                SPUtils.putBean(context, "exceptionCodeInfoList${data.单号}", exceptionCodeInfoList)
                SPUtils.putBean(context, "checkScannedExceptionCodeList${data.单号}", checkScannedExceptionCodeList)
                val bundle = Bundle()
                bundle.putSerializable("exceptionCodeInfoList", exceptionCodeInfoList as Serializable)
                exceptionListFragment.arguments = bundle
                exceptionListFragment.updateList()
              /*  ToastUtils.showLongToast(
                    this@CenterOutSendDetailActivity,
                    exceptionCodeInfoList.toString()
                )*/
                if (!dialogFlag) {
                    //This dialogFlag prevents repeated display.
                    dialogFlag = true
                    DialogUtils.instance()
                        .customDialogYes(this@CenterOutSendDetailActivity)
                        .setTitle("提示")
                        .setMessage(
                            "异常箱码: \n$scanCode\n" +
                                    "异常原因: \n${checkScanCodeVo.msg}"
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
    private fun setRbtnExceptionTextColor() {
        if (exceptionCodeInfoList.size > 0) {
            rbExceptionList.textColor = resources.getColor(R.color.red)
        } else {
            rbExceptionList.setTextColor(resources.getColorStateList(R.color.selector_radio_btn_textcolor))
        }
    }


    /**
     * Creates the ToolBar。
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolbarMenu = setToolBarMenu(arrayListOf("清除缓存"))
        searchView = setSearchView()
        initToolbarItemListener()
        return true
    }

    private fun initToolbarItemListener() {
        toolbarMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.wipeCache -> {
                    wipeCacheByWayBillNumber()
                }
            }
            true

        }
    }

    private fun wipeCacheByWayBillNumber() {
        isWipedCache = true
        checkScannedSucceededScanCodeList.clear()
        checkScannedExceptionCodeList.clear()
        //Wiping the cache which records number of boxes with QR code and no code.
        SPUtils.remove(context, "orderDataChanged${data.单号}")
        //Wiping the cache which records number of improper boxes.
        SPUtils.remove(context, "exceptionCodeInfoList${data.单号}")
        //Wiping the cache which records list of QR code of boxes successfully verified.
        SPUtils.remove(context, "checkScannedSucceededScanCodeList${data.单号}")
        //Wiping the cache which records list of QR code of improper boxes
        SPUtils.remove(context, "checkScannedExceptionCodeList${data.单号}")
        centerOutSendDetailPresenter.loadWaybillDetailData(false, data)
    }

    @SuppressLint("HandlerLeak")
    private inner class HandheldScanHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Scanner.BARCODE_READ -> {
                    //Display the bar code read
                    val currentScanCode: String = msg.obj.toString()
                    ToastUtils.showShortToast(this@CenterOutSendDetailActivity, currentScanCode)
                    addScanCodeNum(currentScanCode)
                }
                Scanner.BARCODE_NOREAD -> {
                }
            }
        }
    }

    private fun addScanCodeNum(currentScanCode: String?) {

        if (scanToTal + onCodeToTal >= planBoxTotal) {
            FeedbackUtils.vibrate(
                this@CenterOutSendDetailActivity,
                longArrayOf(100, 100, 100, 100, 100, 100, 100, 100),
                false
            )
            ToastUtils.showShortToast(this@CenterOutSendDetailActivity, "已达到计划总量 $planBoxTotal 箱")
            return
        }
        if (!checkScannedSucceededScanCodeList.contains(currentScanCode) && !checkScannedExceptionCodeList.contains(
                currentScanCode
            )
        ) {
            FeedbackUtils.vibrate(this@CenterOutSendDetailActivity, 200)
            currentScanCodeList.add(currentScanCode)
        } else {
            DialogUtils.instance()
                .customDialogYes(this@CenterOutSendDetailActivity)
                .setTitle("提示")
                .setMessage("此条码\n $currentScanCode \n已扫描!")
                .show()
        }
    }

    /**
     * Updates Num of ScanCode and show it.
     */
    private fun updateScanCodeTotal() {
        scanToTal = 0
        for (mx in orderDataChanged) {
            scanToTal += mx.出库箱数
        }
        tvScanTotal.text = scanToTal.toString()
    }

    /**
     * Initializes scanner of handheld.
     */
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

    override fun onDestroy() {
        threadExit = true
        super.onDestroy()
    }
}
