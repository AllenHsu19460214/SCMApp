package com.bjjc.scmapp.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.barcode.Scanner
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.CenterOutSendListAdapter
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.CenterOutSendMxBean
import com.bjjc.scmapp.presenter.impl.CenterOutSendPresenterImpl
import com.bjjc.scmapp.presenter.interf.CenterOutSendPresenter
import com.bjjc.scmapp.ui.activity.base.BaseScannerActivity
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.ToolbarManager
import com.bjjc.scmapp.util.dialog_custom.DialogDirector
import com.bjjc.scmapp.util.dialog_custom.impl.DialogBuilderYesImpl
import com.bjjc.scmapp.view.CenterOutSendView
import com.common.zxing.CaptureActivity
import com.common.zxing.Intents
import kotlinx.android.synthetic.main.layout_aty_center_out_send.*
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

/**
 *  CenterOutSendActivity
 */
class CenterOutSendActivity : BaseScannerActivity(), ToolbarManager, CenterOutSendView, View.OnClickListener {
    //========================================Field==================================================================================
    companion object {
        //intent key
        const val INTENT_KEY_ORDER_DATUM: String = "orderDatum"
    }
    private val TAG: String = CenterOutSendActivity::class.java.simpleName
    override val context: Context by lazy { this }
    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val centerOutSendPresenter: CenterOutSendPresenter by lazy { CenterOutSendPresenterImpl(this, this) }
    private var searchView: SearchView? = null
    private val centerOutSendListAdapter: CenterOutSendListAdapter by lazy { CenterOutSendListAdapter() }
    private val searchResultData: ArrayList<CenterOutSendMxBean> by lazy { ArrayList<CenterOutSendMxBean>() }
    private lateinit var mxData: ArrayList<CenterOutSendMxBean>
    private lateinit var toolbarMenu: Toolbar
    private  var scanNumber: String?=null
    //========================================/Field==================================================================================
    /**
     * Loads layout of current activity.
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send

    override fun initView() {
        if (!App.isPDA) {
            btnScan.visibility = View.VISIBLE
        }
        initToolBar("配送单出库")
    }

    override fun initListener() {
        srlPullToRefresh.apply {
            //Initialize color of refreshing control.
            setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE)
            //Sets listener of refreshing control.
            setOnRefreshListener {
                //used to clean the searchView content on initialization.
                searchView?.setQuery(null, false)
                centerOutSendPresenter.onLoad()
            }
        }
        btnScan.setOnClickListener(this)
        lvCenterOutSend.setOnItemClickListener { parent, view, position, id ->
            startActivity<CenterOutSendDetailActivity>(INTENT_KEY_ORDER_DATUM to searchResultData[position])
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnScan -> {
                //Go to the camera activity.
                val openCameraIntent = Intent(context, CaptureActivity::class.java)
                intent.action = Intents.Scan.ACTION
                openCameraIntent.putExtra("autoEnlarged", true)
                startActivityForResult(openCameraIntent, 0)
            }
        }
    }

    override fun initData() {
        centerOutSendPresenter.onLoad()
    }

    override fun onScanCodeSuccess(scanCodeResult: String?) {
        scanNumber = scanCodeResult
        searchView?.setQuery(scanNumber, true)
    }

    override fun onScanCodeFailure() {
        ToastUtils.showToastS(context, "未识别的条码!")
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
        toolbarMenu = setToolBarMenu(R.menu.menu_center_out_send, *itemId)
        searchView = setSearchView("输入或扫描单据号码", object : ToolbarManager.ISearchView {
            override fun onSearchClickListener() {
                if (scanNumber != null) {
                    searchView?.setQuery(scanNumber, false)
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
        toolbarMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.billStatusAll -> {
                    searchResultData.clear()
                    searchResultData.addAll(mxData)
                }
                R.id.billStatusApprove -> {
                    val billStatusApprovedData: ArrayList<CenterOutSendMxBean> = ArrayList()
                    for (value in mxData) {
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
                    val billStatusPassedData: ArrayList<CenterOutSendMxBean> = ArrayList()
                    for (value in mxData) {
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
                    val billStatusUndoneData: ArrayList<CenterOutSendMxBean> = ArrayList()
                    for (value in mxData) {
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
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val searchCode = query.trim()
                val searchCodeData: ArrayList<CenterOutSendMxBean> = ArrayList()
                for (value in mxData) {
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
                        DialogBuilderYesImpl(this@CenterOutSendActivity),
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

    override fun onLoadSuccess(data: ArrayList<CenterOutSendMxBean>) {
        //Hides the Icon of the refreshing component.
        srlPullToRefresh.isRefreshing = false
        myToast("获取数据成功!")
        mxData = data
        searchResultData.clear()
        searchResultData.addAll(mxData)
        centerOutSendListAdapter.updateData(searchResultData)
        lvCenterOutSend.adapter = centerOutSendListAdapter
    }

    override fun onError(message: String?) {
        //Hides the Icon of the refreshing component.
        srlPullToRefresh.isRefreshing = false
        message?.let { myToast(message) }
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
