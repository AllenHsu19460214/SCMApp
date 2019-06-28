package com.bjjc.scmapp.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.hardware.barcode.Scanner
import android.support.v7.widget.SearchView
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.bean.OutByDistributionBean
import com.bjjc.scmapp.model.entity.DeviceEntity
import com.bjjc.scmapp.presenter.`interface`.IPresenter
import com.bjjc.scmapp.presenter.impl.OutByDistributionPresenterImpl
import com.bjjc.scmapp.ui.activity.base.BaseScannerAty
import com.bjjc.scmapp.ui.adapter.CenterOutSendListAdapter
import com.bjjc.scmapp.ui.view.IView
import com.bjjc.scmapp.ui.widget.dialog_custom.DialogDirector
import com.bjjc.scmapp.ui.widget.dialog_custom.impl.DialogBuilderYesImpl
import com.bjjc.scmapp.util.ProgressDialogUtils
import com.bjjc.scmapp.util.toolbar.ToolbarManager
import com.common.zxing.CaptureActivity
import com.common.zxing.Intents
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.layout_aty_out_by_distribute_doc.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity

/**
 *  OutByDistributionActivity
 */
class OutByDistributionActivity : BaseScannerAty(), IView, View.OnClickListener {
    override fun onDataSuccess(data:Map<String,Any>) {

        if (progressDialog.isShowing) progressDialog.dismiss()
        //Hides the Icon of the refreshing component.
        srlPullToRefresh.isRefreshing = false
        myToast("获取数据成功!")
        dataList = data["outByDistributionBeanList"] as ArrayList<OutByDistributionBean>
        searchResultData.clear()
        searchResultData.addAll(dataList)
        centerOutSendListAdapter.updateData(searchResultData)
        lvCenterOutSend.adapter = centerOutSendListAdapter
    }

    override fun onDataFailure(data:Map<String,Any>) {
        if (progressDialog.isShowing) progressDialog.dismiss()
        //Hides the Icon of the refreshing component.
        srlPullToRefresh.isRefreshing = false
    }

    //========================================Field==================================================================================
    companion object {
        //intent key
        const val INTENT_KEY_ORDER_DATUM: String = "orderDatum"
    }
    private lateinit var progressDialog: Dialog
    //private val TAG: String = OutByDistributionActivity::class.java.simpleName
    private lateinit var myToolbar: ToolbarManager.MyToolbar
    private val outByDistributionPresenterImpl: IPresenter by lazy { OutByDistributionPresenterImpl(this) }
    private val centerOutSendListAdapter: CenterOutSendListAdapter by lazy { CenterOutSendListAdapter() }
    private val searchResultData: ArrayList<OutByDistributionBean> by lazy { ArrayList<OutByDistributionBean>() }
    private lateinit var dataList: ArrayList<OutByDistributionBean>
    private var scanNumber: String? = null
    //========================================/Field==================================================================================
    /**
     * Loads layout of current activity.
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_out_by_distribute_doc

    override fun initView() {
        if (!DeviceEntity.isPDA) {
            btnScan.visibility = View.VISIBLE
        }
    }

    override fun initListener() {
        srlPullToRefresh.apply {
            //Initialize color of refreshing control.
            setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE)
            //Sets listener of refreshing control.
            setOnRefreshListener {
                //used to clean the searchView content on initialization.
                myToolbar.searchView.setQuery(null, false)
                progressDialog = ProgressDialogUtils.showProgressDialog(this@OutByDistributionActivity, "正在登录中!")
                outByDistributionPresenterImpl.loadData()
            }
        }
        btnScan.setOnClickListener(this)
        lvCenterOutSend.setOnItemClickListener { _, _, position, _ ->
            startActivity<OutByDistributeDocDetailAty>(INTENT_KEY_ORDER_DATUM to searchResultData[position])
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnScan -> {
                //Go to the camera activity.
                val openCameraIntent = Intent(this, CaptureActivity::class.java)
                intent.action = Intents.Scan.ACTION
                openCameraIntent.putExtra("autoEnlarged", true)
                startActivityForResult(openCameraIntent, 0)
            }
        }
    }

    override fun initData() {
        myToolbar = ToolbarManager.Build(this, toolbar)
            .setTitle("配送单出库")
            .create()
        progressDialog = ProgressDialogUtils.showProgressDialog(this, "正在登录中!")
        outByDistributionPresenterImpl.loadData()
    }

    override fun onScanCodeSuccess(scanCodeResult: String?) {
        scanNumber = scanCodeResult
        myToolbar.searchView.setQuery(scanNumber, true)
    }

    override fun onScanCodeFailure() {
        ToastUtils.show("未识别的条码!")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val itemId = intArrayOf(
            R.id.searchView,
            R.id.searchByBillStatus,
            R.id.billStatusAll,
            R.id.billStatusApprove,
            R.id.billStatusPass,
            R.id.billStatusUndone
        )
        myToolbar.setMenuItem(R.menu.menu_center_out_send, *itemId)
        myToolbar.setSearchView("输入或扫描单据号码", object : ToolbarManager.ISearchView {
            override fun onSearchClickListener(it: View) {
                if (scanNumber != null) {
                    (it as SearchView).setQuery(scanNumber, false)
                }
            }

            override fun onQueryTextFocusChange(hasFocus: Boolean) {
                if (!hasFocus) {
                    if (scanNumber != null) {
                        scanNumber = null
                    }
                }
            }
        })
        //Used to search order.
        setMenuItemListener()
        return true
    }

    private fun setMenuItemListener() {
        myToolbar.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.billStatusAll -> {
                    searchResultData.clear()
                    searchResultData.addAll(dataList)
                }
                R.id.billStatusApprove -> {
                    val billStatusApprovedData: ArrayList<OutByDistributionBean> = ArrayList()
                    for (value in dataList) {
                        if (value.单据状态 == ToolbarManager.BILL_STATUS_APPROVE) {
                            billStatusApprovedData.add(value)
                        }
                    }
                    if (billStatusApprovedData.size > 0) {
                        searchResultData.clear()
                        searchResultData.addAll(billStatusApprovedData)
                    } else {
                        myToast("无查询结果")
                    }
                }
                R.id.billStatusPass -> {
                    val billStatusPassedData: ArrayList<OutByDistributionBean> = ArrayList()
                    for (value in dataList) {
                        if (value.单据状态 == ToolbarManager.BILL_STATUS_PASS) {
                            billStatusPassedData.add(value)
                        }
                    }
                    if (billStatusPassedData.size > 0) {
                        searchResultData.clear()
                        searchResultData.addAll(billStatusPassedData)
                    } else {
                        myToast("无查询结果")
                    }
                }
                R.id.billStatusUndone -> {
                    val billStatusUndoneData: ArrayList<OutByDistributionBean> = ArrayList()
                    for (value in dataList) {
                        if (value.单据状态 == ToolbarManager.BILL_STATUS_UNDONE) {
                            billStatusUndoneData.add(value)
                        }
                    }
                    if (billStatusUndoneData.size > 0) {
                        searchResultData.clear()
                        searchResultData.addAll(billStatusUndoneData)
                    } else {
                        myToast("无查询结果")
                    }
                }
            }
            //Updates the data in the adapter to avoid using previous data when user clicks an item of list.
            centerOutSendListAdapter.updateData(searchResultData)
            true

        }

        myToolbar.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val searchCode = query.trim()
                val searchCodeData: ArrayList<OutByDistributionBean> = ArrayList()
                for (value in dataList) {
                    if (value.单号.contains(searchCode)) {
                        searchCodeData.add(value)
                    }
                }
                if (searchCodeData.size > 0) {
                    searchResultData.clear()
                    searchResultData.addAll(searchCodeData)
                    centerOutSendListAdapter.updateData(searchResultData)
                } else {
                    DialogDirector.showDialog(
                        DialogBuilderYesImpl(this@OutByDistributionActivity),
                        "提示",
                        "未在列表中搜索到该物流单号，\n请输入正确的物流单号!"
                    )
                }
                return true
            }

            override fun onQueryTextChange(nextText: String): Boolean {
                //ToastUtils.showToastS(toolbar.context, nextText)
                return true
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.repeatCount == 0) {
            if (keyCode == 4) {
                onBackPressed()
            } else if (keyCode == 211 || keyCode == 212 || keyCode == 220 || keyCode == 221) {
                // start scanning.
                Scanner.Read()
            }
        }
        return true
    }
}
