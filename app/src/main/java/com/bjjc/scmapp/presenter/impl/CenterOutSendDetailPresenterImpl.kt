package com.bjjc.scmapp.presenter.impl

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.*
import com.bjjc.scmapp.presenter.interf.CenterOutSendDetailPresenter
import com.bjjc.scmapp.ui.activity.CenterOutSendDetailActivity
import com.bjjc.scmapp.ui.fragment.DataListFragment
import com.bjjc.scmapp.ui.fragment.ExceptionListFragment
import com.bjjc.scmapp.util.*
import com.bjjc.scmapp.util.dialog_custom.DialogDirector
import com.bjjc.scmapp.util.dialog_custom.impl.DialogBuilderYesImpl
import com.bjjc.scmapp.util.dialog_custom.impl.DialogBuilderYesNoImpl
import com.bjjc.scmapp.util.httpUtils.RetrofitUtils
import com.bjjc.scmapp.util.httpUtils.ServiceApi
import com.bjjc.scmapp.view.CenterOutSendDetailView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Allen on 2019/01/09 9:56
 */
@Suppress("UNCHECKED_CAST")
class CenterOutSendDetailPresenterImpl(var context: Context, var centerOutSendDetailView: CenterOutSendDetailView) :
    CenterOutSendDetailPresenter {

    //==============================================Field===========================================================
    companion object {
        const val DATA_LIST_FRAGMENT: Int = 0
        const val EXCEPTION_LIST_FRAGMENT: Int = 1
    }

    private lateinit var mDatum: CenterOutSendMxBean
    private lateinit var checkQRCodeBean: CheckQRCodeBean
    private val cachedQRCodeList: ArrayList<String> by lazy { ArrayList<String>() }
    private lateinit var centerOutSendDetailBean: CenterOutSendDetailBean
    private val mxData: ArrayList<CenterOutSendDetailMxBean> by lazy { ArrayList<CenterOutSendDetailMxBean>() }
    private val dataListFragment: DataListFragment by lazy { DataListFragment() }
    private val exceptionListFragment: ExceptionListFragment by lazy { ExceptionListFragment() }
    private val exceptionCodeInfoList: ArrayList<ExceptionCodeInfoBean> by lazy { ArrayList<ExceptionCodeInfoBean>() }
    private val cachedExceptionQRCodeList: ArrayList<String> by lazy { ArrayList<String>() }
    private var dialogFlag: Boolean = false
    private var threadExit: Boolean = false
    private var scanToTal: Long = 0
    private var noCodeTotal: Long = 0
    private var planTotal: Long = 0
    private var currentFragment: Fragment? = null
    //No locking is required after using CopyOnWriteArrayList()
    private var queueQRCode: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()

    //==============================================/Field================================================================
    override fun setInitData(datum: CenterOutSendMxBean) {
        mDatum = datum
    }

    override fun readCache() {
        if(SPUtils.contains(context, "mxData${mDatum.单号}")){
            mxData.clear()
            mxData.addAll(
                SPUtils.getBean(
                    context,
                    "mxData${mDatum.单号}"
                ) as ArrayList<CenterOutSendDetailMxBean>
            )
        }

        if (SPUtils.contains(context, "cachedQRCodeList${mDatum.单号}")) {
            cachedQRCodeList.clear()
            cachedQRCodeList.addAll(
                SPUtils.getBean(
                    context,
                    "cachedQRCodeList${mDatum.单号}"
                ) as ArrayList<String>
            )
        }
        if (SPUtils.contains(context, "exceptionCodeInfoList${mDatum.单号}")) {
            exceptionCodeInfoList.clear()
            exceptionCodeInfoList.addAll(

                (SPUtils.getBean(
                    context,
                    "exceptionCodeInfoList${mDatum.单号}"
                ) as ArrayList<ExceptionCodeInfoBean>)
            )
        }
        if (SPUtils.contains(context, "cachedExceptionQRCodeList${mDatum.单号}")) {
            cachedExceptionQRCodeList.clear()
            cachedExceptionQRCodeList.addAll(
                SPUtils.getBean(
                    context,
                    "cachedExceptionQRCodeList${mDatum.单号}"
                ) as ArrayList<String>
            )
        }
        getPlanNum()
        getScanTotal()
        centerOutSendDetailView.updateView(planTotal, scanToTal)
        centerOutSendDetailView.setExceptionTitleColor(exceptionCodeInfoList)
    }

    /**
     * Transmit datum from CenterOutSendDetailActivity to DataListFragment.
     */
    override fun transmitDataToDataListFragment() {
        val bundle = Bundle()
        bundle.putSerializable(
            CenterOutSendDetailActivity.INTENT_KEY_ORDER_DATA,
            mxData as Serializable
        )
        bundle.putString(
            CenterOutSendDetailActivity.INTENT_KEY_ORDER_NUMBER,
            mDatum.单号
        )
        dataListFragment.arguments = bundle
        bundle.putSerializable(
            "exceptionCodeInfoList",
            exceptionCodeInfoList as Serializable
        )
        exceptionListFragment.arguments = bundle
    }

    //Shows the right fragment。
    override fun switchFragment(targetFragmentInt: Int): FragmentTransaction {
        val targetFragment: Fragment =
            when (targetFragmentInt) {
                DATA_LIST_FRAGMENT -> dataListFragment
                EXCEPTION_LIST_FRAGMENT -> exceptionListFragment
                else -> dataListFragment
            }
        val transaction: FragmentTransaction =
            (context as CenterOutSendDetailActivity).supportFragmentManager.beginTransaction()
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

    //start Async thread to scan QR code.
    //Be sure to prevent this method from being started twice
    override fun startScanQRCodeThread() {
        doAsync {
            whileLoop@ while (!threadExit) {
                /*if (scanToTal >= planTotal) {
                    break@whileLoop
                }*/
                if (queueQRCode.isNotEmpty() && queueQRCode.size > 0) {
                    if (!App.offLineFlag) {
                        checkQRCode(queueQRCode.removeAt(0))
                    } else {
                        //opening thread here in order to simulate checking QRCode offline.
                        doAsync {
                            Thread.sleep(3000)
                            checkQRCodeOffLine(queueQRCode.removeAt(0))
                        }

                    }
                }
            }
        }
    }

    override fun enqueueQRCode(scanCodeResult: String?) {
        if (isReachScanTotal()) return
        if (!cachedQRCodeList.contains(scanCodeResult) && !cachedExceptionQRCodeList.contains(scanCodeResult)) {
            FeedbackUtils.vibrate(context, 200)
            queueQRCode.add(scanCodeResult)
        } else {
            DialogDirector.showDialog(
                DialogBuilderYesImpl(context),
                "提示",
                "此条码\n $scanCodeResult \n已扫描!"
            )
        }
    }

    private fun checkQRCode(QRCode: String) {
        RetrofitUtils.getRetrofit(App.base_url).create(ServiceApi::class.java)
            .checkQRCode(
                "18",
                "ZZCCK",
                QRCode,
                mDatum.出库单位,
                mDatum.入库单位,
                mDatum.单号
            ).enqueue(object : Callback<CheckQRCodeBean> {
                override fun onFailure(call: Call<CheckQRCodeBean>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            centerOutSendDetailView.onError(t.toString())
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CheckQRCodeBean>,
                    response: Response<CheckQRCodeBean>
                ) {
                    checkQRCodeBean = response.body() as CheckQRCodeBean
                    increaseQRCodeNum(QRCode)
                    getScanTotal()
                    centerOutSendDetailView.updateView(planTotal, scanToTal)
                    isReachScanTotal()

                }

            })
    }

    override fun setNoCodeToTal(noCodeTotal: Long) {
        this.noCodeTotal = noCodeTotal
        centerOutSendDetailView.onSetNoCodeText(noCodeTotal)
    }

    private fun isReachScanTotal(): Boolean {
        if (scanToTal + noCodeTotal >= planTotal) {
            FeedbackUtils.vibrate(
                context,
                longArrayOf(100, 100, 100, 100, 100, 100, 100, 100),
                false
            )
            ToastUtils.showToastS(context, "已达到计划总量 $planTotal 箱")
            return true
        }
        return false
    }

    private fun increaseQRCodeNum(QRCode: String) {
        when (checkQRCodeBean.code) {
            "08" -> {
                if (cachedQRCodeList.contains(QRCode)) {
                    return
                }
                loop@ for ((index, value) in mxData.withIndex()) {
                    if (checkQRCodeBean.wlbm == value.物料编码) {
                        if (value.出库箱数 + value.出库输入箱数 < value.计划箱数) {
                            mxData[index].出库箱数++
                            cachedQRCodeList.add(QRCode)
                            Thread.sleep(500)
                            dataListFragment.dataListAdapter.notifyDataSetChanged()
                            break@loop
                        } else {
                            FeedbackUtils.vibrate(
                                context,
                                longArrayOf(100, 100, 100, 100, 100, 100, 100, 100),
                                false
                            )
                            ToastUtils.showToastS(context, "该订单:${value.原始订单号}已到达计划箱数!")
                        }
                    }
                }
                SPUtils.putBean(context, "mxData${mDatum.单号}", this.mxData)
                SPUtils.putBean(
                    context,
                    "cachedQRCodeList${mDatum.单号}",
                    cachedQRCodeList
                )
            }
            else -> {
                //Occurs exception code.
                val exceptionCodeInfoBean = ExceptionCodeInfoBean()
                exceptionCodeInfoBean.code = QRCode
                exceptionCodeInfoBean.msg = checkQRCodeBean.msg
                if (exceptionCodeInfoList.contains(exceptionCodeInfoBean)) {
                    return
                }
                cachedExceptionQRCodeList.add(QRCode)
                exceptionCodeInfoList.add(exceptionCodeInfoBean)
                SPUtils.putBean(context, "exceptionCodeInfoList${mDatum.单号}", exceptionCodeInfoList)
                SPUtils.putBean(context, "cachedExceptionQRCodeList${mDatum.单号}", cachedExceptionQRCodeList)
                SPUtils.putBean(context, "mxData${mDatum.单号}", this.mxData)
                val bundle = Bundle()
                bundle.putSerializable("exceptionCodeInfoList", exceptionCodeInfoList as Serializable)
                exceptionListFragment.arguments = bundle
                exceptionListFragment.updateList()
                if (!dialogFlag) {
                    //This dialogFlag prevents repeated display.
                    dialogFlag = true
                    DialogDirector.showDialog(
                        DialogBuilderYesImpl(context),
                        "提示",
                        "异常箱码:\n$QRCode\n异常原因:\n${checkQRCodeBean.msg}",
                        {
                            dialogFlag = false
                        }
                    )
                }
                centerOutSendDetailView.setExceptionTitleColor(exceptionCodeInfoList)
            }

        }
    }

    override fun loadData() {
        if (!App.offLineFlag) {
            loadDataFromServer()
        } else {
            loadDataOffLine()
        }
    }

    /**
     * Gets Detail of orders for goods from waybill.
     */
    private fun loadDataFromServer() {
        val progressDialog =
            ProgressDialogUtils.showProgressDialog(context, "正在获取数据中!")
        RetrofitUtils.getRetrofit(App.base_url).create(ServiceApi::class.java)
            .centerOutSendDetail(
                "3",
                App.loginBean.key,
                mDatum.单号,
                "CK"
            ).enqueue(object : Callback<CenterOutSendDetailBean> {
                override fun onFailure(call: Call<CenterOutSendDetailBean>, t: Throwable) {
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
                    call: Call<CenterOutSendDetailBean>,
                    response: Response<CenterOutSendDetailBean>
                ) {
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                    centerOutSendDetailBean = response.body() as CenterOutSendDetailBean
                    mxData.clear()
                    mxData.addAll(centerOutSendDetailBean.mx)
                    addTestDataToMxData(mxData)
                    getPlanNum()
                    getScanTotal()
                    dataListFragment.updateData(mxData)
                    exceptionCodeInfoList.clear()
                    exceptionListFragment.updateList()
                    centerOutSendDetailView.onLoadSuccess(centerOutSendDetailBean)
                    centerOutSendDetailView.updateView(planTotal, scanToTal)
                    centerOutSendDetailView.setExceptionTitleColor(exceptionCodeInfoList)
                }

            })
    }

    /**
     * Updates Num of ScanCode and show it.
     */
    private fun getScanTotal() {
        scanToTal = 0
        for (mx in mxData) {
            scanToTal += mx.出库箱数
        }
    }

    private fun getPlanNum() {
        planTotal = 0
        for (mx: CenterOutSendDetailMxBean in mxData) {
            planTotal += mx.计划箱数
        }
    }

    /*
    * val gson: Gson = Gson()
            val infoJson = gson.toJson(mxData)
            val traceJson = gson.toJson(cachedQRCodeList)
            val point: String? = GpsUtils.getGPSPointString()
    * */
    //saves order info to server.
    override fun submitOrSaveOrderInfo() {
        val gson: Gson = Gson()
        val infoJson = gson.toJson(mxData)
        val traceJson = gson.toJson(cachedQRCodeList)
        val point: String? = GpsUtils.getGPSPointString()
        val progressDialog = ProgressDialogUtils.showProgressDialog(context, "正在保存数据中!")
        RetrofitUtils.getRetrofit(App.base_url).create(ServiceApi::class.java)
            .centerOutSendDetailSaveOrderInfo(
                "1",
                App.loginBean.key,
                mDatum.单号,
                if (isOrderFinished()) {
                    "已出库"
                } else {
                    "未出完"
                },
                infoJson,
                traceJson,
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
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                    centerOutSendDetailView.onSubmitSuccess(response.body() as CommonResultBean)
                }

            })
    }

    //Determines whether the order is finished.
    override fun isOrderFinished(): Boolean {
        if (planTotal == getFinishedTotal()) {
            return true
        }
        return false
    }

    private fun getFinishedTotal(): Long {
        var actualFinishedTotal: Long = 0
        for (mx: CenterOutSendDetailMxBean in this.mxData) {
            actualFinishedTotal += mx.出库箱数 + mx.出库输入箱数
        }
        return actualFinishedTotal
    }

    /**
     * Add the test datum to order datum.
     */
    private fun addTestDataToMxData(mxData: List<CenterOutSendDetailMxBean>) {
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

    override fun wipeCacheByOrder() {
        if (SPUtils.contains(context, "mxData${mDatum.单号}")||
            SPUtils.contains(context, "exceptionCodeInfoList${mDatum.单号}")||
            SPUtils.contains(context, "cachedQRCodeList${mDatum.单号}")||
            SPUtils.contains(context, "cachedExceptionQRCodeList${mDatum.单号}"))  {
            //customDialogYesOrNo()
            DialogDirector.showDialog(
                DialogBuilderYesNoImpl(context),
                "提示",
                "您确定要清除单号为:\n${mDatum.单号}\n的缓存信息吗?请谨慎清除!",
                {
                    wipeCache()
                }
            )
        } else {
            ToastUtils.showToastS(context, "此单号无缓存信息!")
        }

    }

    override fun wipeCache() {
        cachedQRCodeList.clear()
        cachedExceptionQRCodeList.clear()
        //Wiping the cache which records number of boxes with QR code and no code.
        SPUtils.remove(context, "mxData${mDatum.单号}")
        //Wiping the cache which records number of improper boxes.
        SPUtils.remove(context, "exceptionCodeInfoList${mDatum.单号}")
        //Wiping the cache which records list of QR code of boxes successfully verified.
        SPUtils.remove(context, "cachedQRCodeList${mDatum.单号}")
        //Wiping the cache which records list of QR code of improper boxes
        SPUtils.remove(context, "cachedExceptionQRCodeList${mDatum.单号}")
        loadData()
    }

    //====================================================OffLineData====================================================================
    /**
     * Offline Data
     */
    private fun loadDataOffLine() {
        val centerOutSendDetailVoJson = when (mDatum.单号) {
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
        val datum: CenterOutSendDetailBean =
            gson.fromJson<CenterOutSendDetailBean>(centerOutSendDetailVoJson, CenterOutSendDetailBean::class.java)
        centerOutSendDetailView.onLoadSuccess(datum)
        centerOutSendDetailView.updateView(planTotal, scanToTal)
        centerOutSendDetailView.setExceptionTitleColor(exceptionCodeInfoList)
    }

    private fun checkQRCodeOffLine(scanCode: String) {
        var checkCodeResult = ""
        val gson = Gson()
        val code020Json = readFileUtils.getFromAssets(context, "offline/code-020.json")
        val code530Json = readFileUtils.getFromAssets(context, "offline/code-530.json")
        val code071Json = readFileUtils.getFromAssets(context, "offline/code-071.json")

        val code020List = gson.fromJson<List<String>>(code020Json, object : TypeToken<List<String>>() {}.type)
        val code530List = gson.fromJson<List<String>>(code530Json, object : TypeToken<List<String>>() {}.type)
        val code071List = gson.fromJson<List<String>>(code071Json, object : TypeToken<List<String>>() {}.type)

        when {
            code020List.contains(scanCode) -> checkCodeResult =
                readFileUtils.getFromAssets(context, "offline/checkScanCode08-020.json")
            code530List.contains(scanCode) -> checkCodeResult =
                readFileUtils.getFromAssets(context, "offline/checkScanCode08-530.json")
            code071List.contains(scanCode) -> checkCodeResult =
                readFileUtils.getFromAssets(context, "offline/checkScanCode071.json")
        }
        context.runOnUiThread {
            checkQRCodeBean = gson.fromJson<CheckQRCodeBean>(checkCodeResult, CheckQRCodeBean::class.java)
            increaseQRCodeNum(scanCode)
            getScanTotal()
            centerOutSendDetailView.updateView(planTotal, scanToTal)
            isReachScanTotal()
        }

    }
    //====================================================/OffLineData===================================================================

}