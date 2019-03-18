package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.barcode.Scanner
import android.os.Handler
import android.os.Message
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.CenterOutSendListAdapter
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.common.IntentKey
import com.bjjc.scmapp.model.bean.CenterOutSendBean
import com.bjjc.scmapp.presenter.impl.CenterOutSendPresenterImpl
import com.bjjc.scmapp.presenter.interf.CenterOutSendPresenter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.FeedbackUtils
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.ToolbarManager
import com.bjjc.scmapp.util.dialog_custom.DialogDirector
import com.bjjc.scmapp.util.dialog_custom.impl.DialogBuilderYesImpl
import com.bjjc.scmapp.view.CenterOutSendView
import com.common.zxing.CaptureActivity
import kotlinx.android.synthetic.main.layout_aty_center_out_send.*
import org.jetbrains.anko.find

/**
 *  CenterOutSendActivity
 */
class CenterOutSendActivity : BaseActivity(), ToolbarManager, CenterOutSendView, View.OnClickListener {


    companion object {
        var scanNumber: String? = null
        val TAG: String = CenterOutSendActivity::class.java.simpleName
    }

    override val context: Context by lazy { this }
    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val centerOutSendPresenter: CenterOutSendPresenter by lazy { CenterOutSendPresenterImpl(this, this) }
    private val handheldScanHandler = HandheldScanHandler()
    private var searchView: SearchView? = null
    private val centerOutSendListViewAdapter: CenterOutSendListAdapter by lazy { CenterOutSendListAdapter(this) }
    private val currentData: ArrayList<CenterOutSendBean> by lazy { ArrayList<CenterOutSendBean>() }
    private val originalData: ArrayList<CenterOutSendBean> by lazy { ArrayList<CenterOutSendBean>() }
    private lateinit var toolbarMenu: Toolbar
    /**
     * Loads layout of current activity.
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send

    override fun initView() {
        if (!App.isPDA) {
            btnScan_CenterOutSendActivity.visibility = View.VISIBLE
        }
    }

    override fun initListener() {
        //Initialize color of refreshing control.
        refreshLayout.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE)
        //Sets listener of refreshing control.
        refreshLayout.setOnRefreshListener {
            //clean content of the searchView on initialization
            searchView?.setQuery(null, false)
            centerOutSendPresenter.loadWaybillData(true)
        }
        btnScan_CenterOutSendActivity.setOnClickListener(this)
        lvCenterOutSend.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(
                this@CenterOutSendActivity,
                CenterOutSendDetailActivity::class.java
            )
            intent.putExtra(IntentKey.CENTER_OUT_SEND_AND_CENTER_OUT_SEND_DETAIL_CURRENTDATA, currentData[position])
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnScan_CenterOutSendActivity -> {
                //Enters the page of scan to scan bar code.
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
                ToastUtils.showToastS(this@CenterOutSendActivity, barCodeValue)
                //vibrate Feedback。
                FeedbackUtils.vibrate(this@CenterOutSendActivity, 200)
                scanNumber = barCodeValue
                searchView?.setQuery(barCodeValue, true)
            }
        }
    }

    override fun initData() {
        initToolBar("配送单出库")
        centerOutSendPresenter.loadWaybillData(false)
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
        searchView = setSearchView()
        setToolbarListener()
        return true
    }

    private fun setToolbarListener() {
        toolbarMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.billStatusAll -> {
                    currentData.clear()
                    currentData.addAll(originalData)
                    centerOutSendListViewAdapter.updateData(currentData)
                }
                R.id.billStatusApprove -> {
                    val billStatusApprovedData: ArrayList<CenterOutSendBean> = ArrayList()
                    for (value in originalData) {
                        if (value.单据状态 == ToolbarManager.BILL_STATUS_APPROVE) {
                            billStatusApprovedData.add(value)
                        }
                    }
                    if (billStatusApprovedData.size > 0) {
                        currentData.clear()
                        currentData.addAll(billStatusApprovedData)
                    } else {
                        myToast("无查询结果")
                    }
                }
                R.id.billStatusPass -> {
                    val billStatusPassedData: ArrayList<CenterOutSendBean> = ArrayList()
                    for (value in originalData) {
                        if (value.单据状态 == ToolbarManager.BILL_STATUS_PASS) {
                            billStatusPassedData.add(value)
                        }
                    }
                    if (billStatusPassedData.size > 0) {
                        currentData.clear()
                        currentData.addAll(billStatusPassedData)
                    } else {
                        myToast("无查询结果")
                    }
                }
                R.id.billStatusUndone -> {
                    val billStatusUndoneData: ArrayList<CenterOutSendBean> = ArrayList()
                    for (value in originalData) {
                        if (value.单据状态 == ToolbarManager.BILL_STATUS_UNDONE) {
                            billStatusUndoneData.add(value)
                        }
                    }
                    if (billStatusUndoneData.size > 0) {
                        currentData.clear()
                        currentData.addAll(billStatusUndoneData)
                    } else {
                        myToast("无查询结果")
                    }
                }
            }
            //Updates the data in the adapter to avoid using previous data when user clicks an item of list.
            centerOutSendListViewAdapter.updateData(currentData)
            true

        }

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val searchCode = query.trim()
                val searchCodeData: ArrayList<CenterOutSendBean> = ArrayList()
                for (value in originalData) {
                    if (value.单号.contains(searchCode)) {
                        searchCodeData.add(value)
                    }
                }
                if (searchCodeData.size > 0) {
                    currentData.clear()
                    currentData.addAll(searchCodeData)
                    centerOutSendListViewAdapter.updateData(currentData)
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

    @SuppressLint("HandlerLeak")
    private inner class HandheldScanHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                //Reading the bar code is successful.
                Scanner.BARCODE_READ -> {
                    //Display the bar code read
                    val currentScanCode: String = msg.obj.toString()
                    ToastUtils.showToastS(this@CenterOutSendActivity, currentScanCode)
                    //vibrate Feedback。
                    FeedbackUtils.vibrate(this@CenterOutSendActivity, 200)
                    scanNumber = currentScanCode
                    /*
                     Sets the content of the searchView.
                     param submit : True means that search  immediately.
                     */
                    searchView?.setQuery(currentScanCode, true)
                }
                //Reading the bar code is failing.
                Scanner.BARCODE_NOREAD -> {
                }

            }
        }
    }

    override fun onStart() {
        //assignment value to handler of scanner.
        Scanner.m_handler = handheldScanHandler
        //Initialize the scanning head.
        Scanner.InitSCA()
        super.onStart()
    }

    override fun loadWaybillDataSuccess(data: ArrayList<CenterOutSendBean>) {
        //******************debugStart*******************
        Log.d(TAG, "获取数据成功!")
        Log.d(TAG, data.toString())
        //******************debugEnd********************
        //Hides the refreshing control.
        refreshLayout.isRefreshing = false
        myToast("获取数据成功!")
        originalData.clear()
        originalData.addAll(data)
        currentData.clear()
        currentData.addAll(originalData)
        centerOutSendListViewAdapter.updateData(currentData)
        lvCenterOutSend.adapter = centerOutSendListViewAdapter
        lvCenterOutSend.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(
                this@CenterOutSendActivity,
                CenterOutSendDetailActivity::class.java
            )
            intent.putExtra(IntentKey.CENTER_OUT_SEND_AND_CENTER_OUT_SEND_DETAIL_CURRENTDATA, currentData[position])
            startActivity(intent)
        }
    }

    override fun onError(message: String?) {
        //******************debugStart*******************
        Log.d(TAG, "获取数据失败!")
        Log.d(TAG, message)
        //******************debugEnd********************
        refreshLayout.isRefreshing = false
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
