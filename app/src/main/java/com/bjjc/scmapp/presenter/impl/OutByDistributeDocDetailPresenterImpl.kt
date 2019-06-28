package com.bjjc.scmapp.presenter.impl

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.*
import com.bjjc.scmapp.model.entity.NetEntity
import com.bjjc.scmapp.model.entity.VersionEntity
import com.bjjc.scmapp.presenter.`interface`.IOutByDistributeDocDetailPresenter
import com.bjjc.scmapp.ui.activity.OutByDistributeDocDetailAty
import com.bjjc.scmapp.ui.fragment.DataListFragment
import com.bjjc.scmapp.ui.fragment.ExceptionListFragment
import com.bjjc.scmapp.ui.view.IOutByDistributeDocDetailView
import com.bjjc.scmapp.ui.widget.dialog_custom.DialogDirector
import com.bjjc.scmapp.ui.widget.dialog_custom.impl.DialogBuilderYesImpl
import com.bjjc.scmapp.ui.widget.dialog_custom.impl.DialogBuilderYesNoImpl
import com.bjjc.scmapp.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.toast.ToastUtils
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
class OutByDistributeDocDetailPresenterImpl(var context: Context, var centerOutSendDetailView: IOutByDistributeDocDetailView) :
    IOutByDistributeDocDetailPresenter {


    //==============================================Field===========================================================
    companion object {
        const val DATA_LIST_FRAGMENT: Int = 0
        const val EXCEPTION_LIST_FRAGMENT: Int = 1
        var threadExit: Boolean = false
        var exitFlag:Boolean= true
    }

    private lateinit var centerOutSendDetailBean: OutByDistributeDocOrderInfoBean
    private lateinit var mDatum: OutByDistributionBean
    private lateinit var checkQRCodeBean: CheckQRCodeInfoBean
    private val cachedQRCodeList: ArrayList<String> by lazy { ArrayList<String>() }
    private val mxData: ArrayList<OutByDistributeDocOrderDetailBean> by lazy { ArrayList<OutByDistributeDocOrderDetailBean>() }
    private val dataListFragment: DataListFragment by lazy { DataListFragment() }
    private val exceptionListFragment: ExceptionListFragment by lazy { ExceptionListFragment() }
    private val exceptionCodeInfoList: ArrayList<ExceptionQRCodeInfoBean> by lazy { ArrayList<ExceptionQRCodeInfoBean>() }
    private val cachedExceptionQRCodeList: ArrayList<String> by lazy { ArrayList<String>() }
    private var dialogFlag: Boolean = false
    private var scanToTal: Long = 0
    private var noCodeTotal: Long = 0
    private var planTotal: Long = 0
    private var currentFragment: Fragment? = null
    private var mOutType: String = ""
    private var queueQRCode: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()

    //==============================================/Field================================================================
    override fun setInitData(datum: OutByDistributionBean) {
        mDatum = datum
    }
    override fun isExistCache(): Boolean {
        return SPUtils.contains("mxData${mDatum.单号}") ||
                SPUtils.contains("exceptionCodeInfoList${mDatum.单号}") ||
                SPUtils.contains("cachedQRCodeList${mDatum.单号}") ||
                SPUtils.contains("cachedExceptionQRCodeList${mDatum.单号}")
    }
    override fun readCache() {
        if (SPUtils.contains("mxData${mDatum.单号}")) {
            mxData.clear()
            mxData.addAll(
                SPUtils.getBean(
                    context,
                    "mxData${mDatum.单号}"
                ) as ArrayList<OutByDistributeDocOrderDetailBean>
            )
            centerOutSendDetailView.onReturnOutType(SPUtils.get("outType${mDatum.单号}", "1") as String)
        }

        if (SPUtils.contains("cachedQRCodeList${mDatum.单号}")) {
            cachedQRCodeList.clear()
            cachedQRCodeList.addAll(
                SPUtils.getBean(
                    context,
                    "cachedQRCodeList${mDatum.单号}"
                ) as ArrayList<String>
            )
        }
        if (SPUtils.contains("exceptionCodeInfoList${mDatum.单号}")) {
            exceptionCodeInfoList.clear()
            exceptionCodeInfoList.addAll(

                (SPUtils.getBean(
                    context,
                    "exceptionCodeInfoList${mDatum.单号}"
                ) as ArrayList<ExceptionQRCodeInfoBean>)
            )
        }
        if (SPUtils.contains("cachedExceptionQRCodeList${mDatum.单号}")) {
            cachedExceptionQRCodeList.clear()
            cachedExceptionQRCodeList.addAll(
                SPUtils.getBean(
                    context,
                    "cachedExceptionQRCodeList${mDatum.单号}"
                ) as ArrayList<String>
            )
        }
        getPlannedTotal()
        getScannedTotal()
        centerOutSendDetailView.updateView(planTotal, scanToTal)
        centerOutSendDetailView.setExceptionTitleColor(exceptionCodeInfoList)
    }

    /**
     * Transmit datum from OutByDistributeDocDetailAty to DataListFragment.
     */
    override fun transmitDataToDataListFragment() {
        val bundle = Bundle()
        bundle.putSerializable(
            OutByDistributeDocDetailAty.INTENT_KEY_ORDER_DATA,
            mxData as Serializable
        )
        bundle.putString(
            OutByDistributeDocDetailAty.INTENT_KEY_ORDER_NUMBER,
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
            (context as OutByDistributeDocDetailAty).supportFragmentManager.beginTransaction()
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
        threadExit=false
        doAsync {
            whileLoop@ while (!threadExit) {
                /*if (scanToTal >= planTotal) {
                    break@whileLoop
                }*/
                if (queueQRCode.isNotEmpty() && queueQRCode.size > 0) {
                    if (!VersionEntity.isOffline) {
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
            startScanQRCodeThread()
            exitFlag=false
        } else {
            DialogDirector.showDialog(
                DialogBuilderYesImpl(context),
                "提示",
                "此条码\n $scanCodeResult \n已扫描!"
            )
        }
    }

    private fun getOutType(any: Map<String, Any>): String {
        val ckdwType = any["出库单位类型"] as String
        val fxdd = any["反向订单"] as Int
        return if (1 == fxdd) {
            when (ckdwType) {
                "销售店" -> "XSDCK"
                else -> ""
            }
        } else {
            when (ckdwType) {
                "中转仓" -> "ZZCCK"
                "中心库", "自建中心库" -> "ZXKCK"
                else -> ""
            }
        }
    }

    private fun checkQRCode(QRCode: String) {
        ToastUtils.show("已发送验证！")
        exitFlag=true
        NetEntity.serviceController
            .checkQRCode(
                "18",
                mOutType,
                QRCode,
                centerOutSendDetailBean.出库单位,
                centerOutSendDetailBean.入库单位,
                centerOutSendDetailBean.单号
            ).enqueue(object : Callback<CheckQRCodeInfoBean> {
                override fun onFailure(call: Call<CheckQRCodeInfoBean>, t: Throwable) {
                    doAsync {
                        Thread.sleep(2000)
                        uiThread {
                            centerOutSendDetailView.onError(t.toString())
                            exitFlag=true
                        }
                    }
                }

                override fun onResponse(
                    call: Call<CheckQRCodeInfoBean>,
                    response: Response<CheckQRCodeInfoBean>
                ) {
                    response.body()?.let { checkQRCodeBean=it }
                    increaseQRCodeNum(QRCode)
                    getScannedTotal()
                    centerOutSendDetailView.updateView(planTotal, scanToTal)
                    isReachScanTotal()
                    exitFlag=true
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
            ToastUtils.show("已达到计划总量 $planTotal 箱")
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
                            ToastUtils.show("该订单:${value.原始订单号}已到达计划箱数!")
                        }
                    }
                }
                SPUtils.putBean(context, "mxData${centerOutSendDetailBean.单号}", mxData)
                SPUtils.putBean(
                    context,
                    "cachedQRCodeList${centerOutSendDetailBean.单号}",
                    cachedQRCodeList
                )
            }
            else -> {
                //Occurs exception code.
                val exceptionCodeInfoBean = ExceptionQRCodeInfoBean()
                exceptionCodeInfoBean.code = QRCode
                exceptionCodeInfoBean.msg = checkQRCodeBean.msg
                if (exceptionCodeInfoList.contains(exceptionCodeInfoBean)) {
                    return
                }
                cachedExceptionQRCodeList.add(QRCode)
                exceptionCodeInfoList.add(exceptionCodeInfoBean)
                SPUtils.putBean(context, "exceptionCodeInfoList${centerOutSendDetailBean.单号}", exceptionCodeInfoList)
                SPUtils.putBean(
                    context,
                    "cachedExceptionQRCodeList${centerOutSendDetailBean.单号}",
                    cachedExceptionQRCodeList
                )
                SPUtils.putBean(context, "mxData${centerOutSendDetailBean.单号}", mxData)
                val bundle = Bundle()
                bundle.putSerializable("exceptionCodeInfoList", exceptionCodeInfoList as Serializable)
                exceptionListFragment.arguments = bundle
                exceptionListFragment.updateList()
                if (!dialogFlag) {
                    //This dialogFlag prevents the repeated display of the dialog which shows the exception to product's QR code.
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
        if (!VersionEntity.isOffline) {
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
        NetEntity.serviceController
            .centerOutSendDetail(
                "3",
                App.sKey,
                mDatum.单号,
                "CK"
            ).enqueue(object : Callback<OutByDistributeDocOrderInfoBean> {
                override fun onFailure(call: Call<OutByDistributeDocOrderInfoBean>, t: Throwable) {
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
                    call: Call<OutByDistributeDocOrderInfoBean>,
                    response: Response<OutByDistributeDocOrderInfoBean>
                ) {
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                    centerOutSendDetailBean = response.body() as OutByDistributeDocOrderInfoBean
                    val mapType = HashMap<String, Any>()
                    mapType["反向订单"] = centerOutSendDetailBean.反向订单
                    mapType["出库单位类型"] = centerOutSendDetailBean.出库单位类型
                    mOutType = getOutType(mapType)
                    centerOutSendDetailView.onReturnOutType(mOutType)
                    mxData.clear()
                    mxData.addAll(centerOutSendDetailBean.mx)
                    addTestDataToMxData(mxData)
                    getPlannedTotal()
                    getScannedTotal()
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
    private fun getScannedTotal() {
        scanToTal = 0
        for (mx in mxData) {
            scanToTal += mx.出库箱数
        }
    }

    private fun getPlannedTotal() {
        planTotal = 0
        for (mx: OutByDistributeDocOrderDetailBean in mxData) {
            planTotal += mx.计划箱数
        }
    }

    //saves order info to server.
    override fun submitOrderInfo() {
        val gson: Gson = Gson()
        mxData.map {
            it.数量=it.出库箱数
            it.输入箱数 = it.出库输入箱数
        }
        val infoJson = gson.toJson(mxData)
        val traceJson = gson.toJson(cachedQRCodeList)
        val point: String? = GpsUtils.getGPSPointString(context)
        val progressDialog = ProgressDialogUtils.showProgressDialog(context, "正在保存数据中!")
        NetEntity.serviceController
            .centerOutSendDetailSaveOrderInfo(
                "1",
                App.sKey,
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
            ).enqueue(object : Callback<CommonInfoBean> {
                override fun onFailure(call: Call<CommonInfoBean>, t: Throwable) {
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
                    call: Call<CommonInfoBean>,
                    response: Response<CommonInfoBean>
                ) {
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                    centerOutSendDetailView.onSubmitSuccess(response.body() as CommonInfoBean)
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
        for (mx: OutByDistributeDocOrderDetailBean in mxData) {
            actualFinishedTotal += mx.出库箱数 + mx.出库输入箱数
        }
        return actualFinishedTotal
    }

    /**
     * Add the test datum to order datum.
     */
    private fun addTestDataToMxData(mxData: List<OutByDistributeDocOrderDetailBean>) {
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

    override fun wipeCacheByOrderId() {
        if (SPUtils.contains("mxData${mDatum.单号}") ||
            SPUtils.contains("exceptionCodeInfoList${mDatum.单号}") ||
            SPUtils.contains("cachedQRCodeList${mDatum.单号}") ||
            SPUtils.contains("cachedExceptionQRCodeList${mDatum.单号}")
        ) {
            //customDialogYesOrNo()
            DialogDirector.showDialog(
                DialogBuilderYesNoImpl(context),
                "提示",
                "您确定要清除单号为:\n${mDatum.单号}\n的缓存信息吗?请谨慎清除!",
                {
                    informServerWipeCache()
                }
            )
        } else {
            ToastUtils.show("此单号无缓存信息!")
        }

    }

    override fun wipeCache() {
        cachedQRCodeList.clear()
        cachedExceptionQRCodeList.clear()
        //Wiping the cache which records number of boxes with QR code and no code.
        SPUtils.remove( "mxData${mDatum.单号}")
        //Wiping the cache which records number of improper boxes.
        SPUtils.remove("exceptionCodeInfoList${mDatum.单号}")
        //Wiping the cache which records list of QR code of boxes successfully verified.
        SPUtils.remove("cachedQRCodeList${mDatum.单号}")
        //Wiping the cache which records list of QR code of improper boxes
        SPUtils.remove("cachedExceptionQRCodeList${mDatum.单号}")
        SPUtils.remove("outType${mDatum.单号}")
        ToastUtils.show("本地单号${mDatum.单号}的缓存已清除!")
        loadData()
    }

    private fun informServerWipeCache() {
        val progressDialog = ProgressDialogUtils.showProgressDialog(context, "正在清除缓存中!")
        NetEntity.serviceController
            .centerOutSendDetailWipeCache(
                "4",
                mDatum.单号,//This verification of order number from server is at question.It can still pass in this case that is "mDatum.单号+1111111".
                "CK"
            ).enqueue(object : Callback<CommonInfoBean> {
                override fun onFailure(call: Call<CommonInfoBean>, t: Throwable) {
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                        centerOutSendDetailView.onError(t.message)
                    }
                }

                override fun onResponse(call: Call<CommonInfoBean>, response: Response<CommonInfoBean>) {
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                    centerOutSendDetailView.onSubmitSuccess(response.body() as CommonInfoBean)
                }
            })
    }
    fun wipeExceptionCode(){
        val bundle = Bundle()
        exceptionCodeInfoList.clear()
        cachedExceptionQRCodeList.clear()
        bundle.putSerializable("exceptionCodeInfoList", exceptionCodeInfoList as Serializable)
        exceptionListFragment.arguments = bundle
        exceptionListFragment.updateList()
        centerOutSendDetailView.setExceptionTitleColor(exceptionCodeInfoList)
        //Wiping the cache which records number of improper boxes.
        SPUtils.remove("exceptionCodeInfoList${mDatum.单号}")
        //Wiping the cache which records list of QR code of improper boxes
        SPUtils.remove("cachedExceptionQRCodeList${mDatum.单号}")
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
        val centerOutSendDetailBean: OutByDistributeDocOrderInfoBean =
            gson.fromJson<OutByDistributeDocOrderInfoBean>(centerOutSendDetailVoJson, OutByDistributeDocOrderInfoBean::class.java)
        centerOutSendDetailView.onLoadSuccess(centerOutSendDetailBean)
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
            checkQRCodeBean = gson.fromJson<CheckQRCodeInfoBean>(checkCodeResult, CheckQRCodeInfoBean::class.java)
            increaseQRCodeNum(scanCode)
            getScannedTotal()
            centerOutSendDetailView.updateView(planTotal, scanToTal)
            isReachScanTotal()
        }

    }
    //====================================================/OffLineData===================================================================

}