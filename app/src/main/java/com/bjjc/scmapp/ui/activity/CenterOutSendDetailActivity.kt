package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.barcode.Scanner
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.DataListAdapter
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.*
import com.bjjc.scmapp.presenter.impl.CenterOutSendDetailPresenterImpl
import com.bjjc.scmapp.presenter.interf.CenterOutSendDetailPresenter
import com.bjjc.scmapp.ui.activity.CenterOutSendActivity.Companion.INTENT_KEY_ORDER_DATUM
import com.bjjc.scmapp.ui.activity.base.BaseScannerActivity
import com.bjjc.scmapp.ui.fragment.DataListFragment
import com.bjjc.scmapp.ui.fragment.ExceptionListFragment
import com.bjjc.scmapp.util.*
import com.bjjc.scmapp.util.dialog_custom.DialogDirector
import com.bjjc.scmapp.util.dialog_custom.impl.DialogBuilderYesImpl
import com.bjjc.scmapp.util.dialog_custom.impl.DialogBuilderYesNoImpl
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import com.bjjc.scmapp.view.CenterOutSendDetailView
import com.common.zxing.CaptureActivity
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
@Suppress("UNCHECKED_CAST")
class CenterOutSendDetailActivity : BaseScannerActivity(), ToolbarManager, CenterOutSendDetailView {

    //==============================================FieldStart=====================================================================
    companion object {
        //intent key
        const val INTENT_KEY_ORDER_DATA: String = "mxData"
        const val INTENT_KEY_ORDER_NUMBER: String = "orderNumber"
    }

    override val context: Context by lazy { this }
    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private lateinit var datum: CenterOutSendMxBean
    private lateinit var checkQRCodeBean: CheckQRCodeBean
    private val mxData: ArrayList<CenterOutSendDetailMxBean> by lazy { ArrayList<CenterOutSendDetailMxBean>() }
    private var scanToTal: Long = -1
    private var onCodeToTal: Long = -1
    private var planTotal: Long = 0
    private val dataListFragment: DataListFragment by lazy { DataListFragment() }
    private val exceptionListFragment: ExceptionListFragment = ExceptionListFragment()
    private var currentFragment: Fragment? = null
    private var exceptionCodeInfoList: ArrayList<ExceptionCodeInfoBean> = ArrayList()
    private var cachedExceptionQRCodeList: ArrayList<String> = ArrayList()
    @Volatile
    private var queueQRCode: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()
    private val cachedQRCodeList: ArrayList<String> = ArrayList()
    private var dialogFlag: Boolean = false
    private var threadExit: Boolean = false
    private lateinit var toolbarMenu: Toolbar
    private val centerOutSendDetailPresenter: CenterOutSendDetailPresenter by lazy {
        CenterOutSendDetailPresenterImpl(
            this,
            this
        )
    }

    //==============================================FieldEnd=====================================================================
    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send_detail

    override fun initView() {
        if (!App.isPDA) {
            btnScan.visibility = View.VISIBLE
        }
        tvNoCodeTotal.text = DataListAdapter.noCodeCount
        onCodeToTal = DataListAdapter.noCodeCount.toLong()
    }

    //Shows the right fragment。
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
                R.id.rbExceptionList -> {
                    switchFragment(exceptionListFragment).commit()
                }
            }

        }
        btnSubmit.setOnClickListener {
            // Showing dialog of finished or unfinished to be used to commit or save info of OutOrder.
            if (isOrderFinished()) {
                DialogDirector.showDialog(
                    DialogBuilderYesNoImpl(this@CenterOutSendDetailActivity),
                    "提示",
                    "订单已完成，要提交到服务器吗?",
                    {
                        submitOrderInfo(isOrderFinished())
                    }
                )
            } else {
                DialogDirector.showDialog(
                    DialogBuilderYesNoImpl(this@CenterOutSendDetailActivity),
                    "提示",
                    "订单未完成，要保存到服务器吗?",
                    {
                        submitOrderInfo(isOrderFinished())
                    }
                )
            }
        }
        btnScan.setOnClickListener {
            startActivityForResult(Intent(context, CaptureActivity::class.java).putExtra("autoEnlarged", true), 0)
        }
    }

    //Determines whether the order is finished.
    private fun isOrderFinished(): Boolean {
        if (planTotal == getFinishedTotal()) {
            return true
        }
        return false
    }

    private fun submitOrderInfo(isFinished: Boolean) {
        val gson: Gson = Gson()
        val infoJson = gson.toJson(mxData)
        val traceJson = gson.toJson(cachedQRCodeList)
        val point: String? = GpsUtils.getGPSPointString()
        centerOutSendDetailPresenter.submitOrSaveOrderInfo(isFinished, datum, infoJson, traceJson, point)
    }

    override fun initData() {
        initToolBar("出库", "扫描信息")
        datum = intent.getSerializableExtra(INTENT_KEY_ORDER_DATUM) as CenterOutSendMxBean
        tvWaybillNumber.text = datum.单号
        //start Async thread to scan QR code.
        startScanQRCodeThread()
        /**
         * after sp save.
         */
        if (SPUtils.contains(context, "mxData${datum.单号}")) {
            this.mxData.clear()
            this.mxData.addAll(
                SPUtils.getBean(
                    context,
                    "mxData${datum.单号}"
                ) as ArrayList<CenterOutSendDetailMxBean>
            )
            if (SPUtils.contains(context, "cachedQRCodeList${datum.单号}")) {
                cachedQRCodeList.clear()
                cachedQRCodeList.addAll(
                    SPUtils.getBean(
                        context,
                        "cachedQRCodeList${datum.单号}"
                    ) as ArrayList<String>
                )
            }
            if (SPUtils.contains(context, "cachedExceptionQRCodeList${datum.单号}")) {
                cachedExceptionQRCodeList.clear()
                cachedExceptionQRCodeList.addAll(
                    SPUtils.getBean(
                        context,
                        "cachedExceptionQRCodeList${datum.单号}"
                    ) as ArrayList<String>
                )
            }

        } else {
            centerOutSendDetailPresenter.loadData(datum)
        }
        setPlanTotalText()
        setScanTotalText()
        //Transmit datum from CenterOutSendDetailActivity to DataListFragment.
        transmitData2DataListFragment()
        setRbtnExceptionTextColor()
    }

    override fun onScanCodeSuccess(scanCodeResult: String?) {
        enqueueQRCode(scanCodeResult)
    }

    override fun onScanCodeFailure() {
        ToastUtils.showToastS(context, "未识别的条码!")
    }

    private fun startScanQRCodeThread() {
        doAsync {
            whileLoop@ while (!threadExit) {
                if (scanToTal >= planTotal) {
                    break@whileLoop
                }
                if (queueQRCode.isNotEmpty() && queueQRCode.size > 0) {
                    if (App.offLineFlag) {
                        doAsync {
                            Thread.sleep(3000)
                            checkQRCodeOffLine(queueQRCode.removeAt(0))
                        }
                    } else {
                        checkQRCode(queueQRCode.removeAt(0))
                    }
                }
            }
        }
    }

    override fun onError(message: String?) {
        message?.let { myToast(it) }
    }

    override fun onLoadSuccess(data: CenterOutSendDetailBean) {
        if (data.code == "10") {
            mxData.clear()
            mxData.addAll(data.mx)
            //Sets order number.
            tvWaybillNumber.text = data.单号
            //setNoCodeIsAllowed(datum)//value "仓是否输入" of the main order into disuse
            addTestData2MxData(mxData)
            setPlanTotalText()
            setScanTotalText()
            dataListFragment.updateData(mxData)
            exceptionCodeInfoList.clear()
            setRbtnExceptionTextColor()
            exceptionListFragment.updateList()

        } else {
            myToast(data.msg)
        }
    }

    /**
     * Transmit datum from CenterOutSendDetailActivity to DataListFragment.
     */
    private fun transmitData2DataListFragment() {
        val bundle = Bundle()
        bundle.putSerializable(
            INTENT_KEY_ORDER_DATA,
            this.mxData as Serializable
        )
        bundle.putString(
            INTENT_KEY_ORDER_NUMBER,
            datum.单号
        )
        dataListFragment.arguments = bundle

        if (null != SPUtils.getBean(context, "exceptionCodeInfoList${datum.单号}")) {
            exceptionCodeInfoList.clear()
            exceptionCodeInfoList.addAll(
                SPUtils.getBean(
                    context,
                    "exceptionCodeInfoList${datum.单号}"
                ) as ArrayList<ExceptionCodeInfoBean>
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

    override fun onSubmitSuccess(data: CommonResultBean) {
        if (data.code == "08") {
            //Wiping the cache which correspond to order.
            //SPUtils.remove(context, "mxData${datum.单号}")
            if (SPUtils.contains(UIUtils.getContext(), "mxData${datum.单号}")) {
                wipeCache()
            }
            myToast("保存数据成功!")
        } else {
            myToast(data.msg)
        }
    }

    /**
     * Sets whether noCodeCount input is allowed
     * 1-scan QR code，0-input number
     */
    private fun setNoCodeIsAllowed(data: CenterOutSendDetailBean) {
        if (data.仓是否输入 == 0) {
            for (index: Int in 0 until this.mxData.size) {
                this.mxData[index].是否允许扫描 = 0
            }
        }
    }

    /**
     * Add the test datum to order datum.
     */
    private fun addTestData2MxData(mxData: List<CenterOutSendDetailMxBean>) {
        for ((index, valueTest) in mxData.withIndex()) {
            when (valueTest.原始订单号) {
                "2906261286-2-4" -> {
                    mxData[index].出库箱数 += 25
                    //mxData[indexTest].允许输入箱数+=100
                }
                "2906261800-1-1" -> {
                    mxData[index].出库箱数 += 10
                    //mxData[indexTest].允许输入箱数+=101
                }
                "2906261800-1-2" -> {
                    mxData[index].出库箱数 += 5
                    //mxData[indexTest].允许输入箱数+=102
                }
                "2906261800-2-1" -> {
                    mxData[index].出库箱数 += 15
                    //mxData[indexTest].允许输入箱数+=103
                }
                "FOCEJS059201810250002-1" -> {
                    mxData[index].出库箱数 += 20
                }
                "Z3B054M2018111242" -> {
                    mxData[index].出库箱数 += 25
                }
            }
        }
    }




    private fun checkQRCode(QRCode: String) {
        RetrofitUtils.getRetrofit(App.base_url).create(ServiceApi::class.java)
            .checkQRCode(
                "18",
                "ZXKCK",
                QRCode,
                datum.出库单位,
                datum.入库单位,
                datum.单号
            ).enqueue(object : Callback<CheckQRCodeBean> {
                override fun onFailure(call: Call<CheckQRCodeBean>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            myToast(t.toString())
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CheckQRCodeBean>,
                    response: Response<CheckQRCodeBean>
                ) {
                    checkQRCodeBean = response.body() as CheckQRCodeBean
                    increaseQRCodeNum(QRCode)
                    setRbtnExceptionTextColor()
                    getScanTotal()
                    isReachScanTotal()
                }

            })
    }


    private fun increaseQRCodeNum(scanCode: String) {
        when (checkQRCodeBean.code) {
            "08" -> {
                if (cachedQRCodeList.contains(scanCode)) {
                    return
                }
                loop@ for ((index, value) in this.mxData.withIndex()) {
                    if (checkQRCodeBean.wlbm == value.物料编码) {
                        if (value.出库箱数 + value.出库输入箱数 < value.计划箱数) {
                            this.mxData[index].出库箱数++
                            cachedQRCodeList.add(scanCode)
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
                SPUtils.putBean(context, "mxData${datum.单号}", this.mxData)
                SPUtils.putBean(
                    context,
                    "cachedQRCodeList${datum.单号}",
                    cachedQRCodeList
                )
            }
            "071" -> {
                //Occurs exception code.
                val exceptionCodeInfoBean = ExceptionCodeInfoBean()
                exceptionCodeInfoBean.code = scanCode
                exceptionCodeInfoBean.msg = checkQRCodeBean.msg
                if (exceptionCodeInfoList.contains(exceptionCodeInfoBean)) {
                    return
                }
                cachedExceptionQRCodeList.add(scanCode)
                exceptionCodeInfoList.add(exceptionCodeInfoBean)
                SPUtils.putBean(context, "exceptionCodeInfoList${datum.单号}", exceptionCodeInfoList)
                SPUtils.putBean(context, "cachedExceptionQRCodeList${datum.单号}", cachedExceptionQRCodeList)
                val bundle = Bundle()
                bundle.putSerializable("exceptionCodeInfoList", exceptionCodeInfoList as Serializable)
                exceptionListFragment.arguments = bundle
                exceptionListFragment.updateList()
                if (!dialogFlag) {
                    //This dialogFlag prevents repeated display.
                    dialogFlag = true
                    DialogDirector.showDialog(
                        DialogBuilderYesImpl(this@CenterOutSendDetailActivity),
                        "提示",
                        "异常箱码:\n$scanCode\n异常原因:\n${checkQRCodeBean.msg}",
                        {
                            dialogFlag = false
                        }
                    )
                }

            }

        }
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
        val itemId = intArrayOf(R.id.reduceBox, R.id.wipeCache)
        toolbarMenu = setToolBarMenu(R.menu.menu_center_out_send_detail, *itemId)
        setToolbarListener()
        return true
    }

    private fun setToolbarListener() {
        toolbarMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.reduceBox -> {
                    //myToast("\"减箱\" is clicked!")
                    context.startActivity<CenterOutSendReduceBoxActivity>("datum" to datum)
                }
                R.id.wipeCache -> {
                    wipeCacheByWayBillNumber()
                }
            }
            true
        }
    }

    private fun wipeCacheByWayBillNumber() {
        if (SPUtils.contains(UIUtils.getContext(), "mxData${datum.单号}")) {
            //customDialogYesOrNo()
            DialogDirector.showDialog(
                DialogBuilderYesNoImpl(this@CenterOutSendDetailActivity),
                "提示",
                "您确定要清除单号为:\n${datum.单号}\n的缓存信息吗?请谨慎清除!",
                {
                    wipeCache()
                }
            )
        } else {
            myToast("此单号无缓存信息!")
        }

    }

    private fun wipeCache() {
        cachedQRCodeList.clear()
        cachedExceptionQRCodeList.clear()
        //Wiping the cache which records number of boxes with QR code and no code.
        SPUtils.remove(context, "mxData${datum.单号}")
        //Wiping the cache which records number of improper boxes.
        SPUtils.remove(context, "exceptionCodeInfoList${datum.单号}")
        //Wiping the cache which records list of QR code of boxes successfully verified.
        SPUtils.remove(context, "cachedQRCodeList${datum.单号}")
        //Wiping the cache which records list of QR code of improper boxes
        SPUtils.remove(context, "cachedExceptionQRCodeList${datum.单号}")
        centerOutSendDetailPresenter.loadData(datum)
    }

    private fun enqueueQRCode(QRCode: String?) {

        if (isReachScanTotal()) return
        if (!cachedQRCodeList.contains(QRCode) && !cachedExceptionQRCodeList.contains(QRCode)) {
            FeedbackUtils.vibrate(this@CenterOutSendDetailActivity, 200)
            queueQRCode.add(QRCode)
        } else {
            DialogDirector.showDialog(
                DialogBuilderYesImpl(this@CenterOutSendDetailActivity),
                "提示",
                "此条码\n $QRCode \n已扫描!"
            )
        }
    }

    /**
     * Updates Num of ScanCode and show it.
     */
    private fun getScanTotal() {
        scanToTal = 0
        for (mx in this.mxData) {
            scanToTal += mx.出库箱数
        }
    }

    private fun setScanTotalText() {
        getScanTotal()
        tvScanTotal.text = scanToTal.toString()
    }

    private fun getPlanNum() {
        planTotal = 0
        for (mx: CenterOutSendDetailMxBean in this.mxData) {
            planTotal += mx.计划箱数
        }
    }

    private fun setPlanTotalText() {
        getPlanNum()
        //Sets planning box count
        tvPlanBoxTotal.text = planTotal.toString()
    }

    private fun getFinishedTotal(): Long {
        var actualFinishedTotal: Long = 0
        for (mx: CenterOutSendDetailMxBean in this.mxData) {
            actualFinishedTotal += mx.出库箱数 + mx.出库输入箱数
        }
        return actualFinishedTotal
    }


    private fun isReachScanTotal(): Boolean {
        if (scanToTal + onCodeToTal >= planTotal) {
            FeedbackUtils.vibrate(
                this@CenterOutSendDetailActivity,
                longArrayOf(100, 100, 100, 100, 100, 100, 100, 100),
                false
            )
            ToastUtils.showToastS(this@CenterOutSendDetailActivity, "已达到计划总量 $planTotal 箱")
            return true
        }
        return false
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
    //====================================================OffLineData===================================================================
    private fun checkQRCodeOffLine(scanCode: String) {
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
            checkQRCodeBean = gson.fromJson<CheckQRCodeBean>(checkCodeResult, CheckQRCodeBean::class.java)
            increaseQRCodeNum(scanCode)
            setRbtnExceptionTextColor()
            getScanTotal()
            isReachScanTotal()
        }

    }
    //===================================================/OffLineData===================================================================
}
