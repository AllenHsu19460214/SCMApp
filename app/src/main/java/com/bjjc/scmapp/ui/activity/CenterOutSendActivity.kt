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
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.CenterOutSendListAdapter
import com.bjjc.scmapp.model.bean.CenterOutSendBean
import com.bjjc.scmapp.presenter.impl.CenterOutSendPresenterImpl
import com.bjjc.scmapp.presenter.interf.CenterOutSendPresenter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.DialogUtils
import com.bjjc.scmapp.util.FeedbackUtils
import com.bjjc.scmapp.util.ToastUtils
import com.bjjc.scmapp.util.ToolbarManager
import com.bjjc.scmapp.view.CenterOutSendView
import kotlinx.android.synthetic.main.layout_aty_center_out_send.*
import org.jetbrains.anko.find

/**
 *  CenterOutSendActivity
 */
class CenterOutSendActivity : BaseActivity(), ToolbarManager, CenterOutSendView {

    companion object {
        var scanNumber:String?=null
        val TAG:String=CenterOutSendActivity::class.java.simpleName
    }
    override val context: Context by lazy { this }
    override val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val centerOutSendPresenter:CenterOutSendPresenter by lazy { CenterOutSendPresenterImpl(this,this) }
    private val handheldScanHandler = HandheldScanHandler()
    private  var searchView:SearchView?=null
    private val centerOutSendListViewAdapter: CenterOutSendListAdapter by lazy { CenterOutSendListAdapter(this) }
    private lateinit var data:ArrayList<CenterOutSendBean>
    private lateinit var toolbarMenu:Toolbar
    /**
     * Loads layout of current activity.
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send

    override fun initListener() {
        //Initialize color of refreshing control.
        refreshLayout.setColorSchemeColors( Color.GREEN,Color.RED, Color.BLUE)
        //Sets listener of refreshing control.
        refreshLayout.setOnRefreshListener {
            searchView?.setQuery( null,false)
            centerOutSendPresenter.loadWaybillData(true)
        }
    }
    override fun initData() {
        initCenterOutSendToolBar()
        centerOutSendPresenter.loadWaybillData(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolbarMenu=setToolBarMenu(arrayListOf(
            ToolbarManager.SEARCH,
            ToolbarManager.SEARCH_BY_BILL_STATUS,
            ToolbarManager.BILL_STATUS_ALL,
            ToolbarManager.BILL_STATUS_APPROVE,
            ToolbarManager.BILL_STATUS_PASS,
            ToolbarManager.BILL_STATUS_UNDONE))
        searchView = setSearchView()
        initToolbarItemListener()
        return true
    }

    private fun initToolbarItemListener() {
        toolbarMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.billStatusAll->{
                    centerOutSendListViewAdapter.updateData(data)
                }
                R.id.billStatusApprove->{
                    val billStatusApproveData:ArrayList<CenterOutSendBean> = ArrayList()
                    for (value in data){
                        if (value.单据状态==ToolbarManager.BILL_STATUS_APPROVE) {
                            billStatusApproveData.add(value)
                        }
                    }
                    centerOutSendListViewAdapter.updateData(billStatusApproveData)
                }
                R.id.billStatusPass->{
                    val billStatusPassData:ArrayList<CenterOutSendBean> = ArrayList()
                    for (value in data){
                        if (value.单据状态==ToolbarManager.BILL_STATUS_PASS) {
                            billStatusPassData.add(value)
                        }
                    }
                    centerOutSendListViewAdapter.updateData(billStatusPassData)
                }
                R.id.billStatusUndone->{
                    val billStatusUndoneData:ArrayList<CenterOutSendBean> = ArrayList()
                    for (value in data){
                        if (value.单据状态==ToolbarManager.BILL_STATUS_UNDONE) {
                            billStatusUndoneData.add(value)
                        }
                    }
                    centerOutSendListViewAdapter.updateData(billStatusUndoneData)
                }
            }
            true
        }
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val searchCode = query.trim()
                val searchCodeData:ArrayList<CenterOutSendBean> = ArrayList()
                for (value in data){
                    if (value.单号==searchCode) {
                        searchCodeData.add(value)
                    }
                }
                if (searchCodeData.size>0){
                    centerOutSendListViewAdapter.updateData(searchCodeData)
                }else{
                    DialogUtils.instance()
                        .customDialogYes(this@CenterOutSendActivity)
                        .setTitle("提示")
                        .setMessage("未在列表中搜索到该物流单号，\n请输入正确的物流单号!")
                        .setOnPositiveClickListener(object : DialogUtils.OnPositiveClickListener {
                            override fun onPositiveBtnClicked() {}
                        })
                        .show()
                    //myToast("未在列表中搜索到该物流单号，\n请输入正确的物流单号!")
                }
                return true
            }
            override fun onQueryTextChange(nextText: String): Boolean {
                //ToastUtils.showShortToast(toolbar.context, nextText)
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
                    val currentScanCode:String=msg.obj.toString()
                    ToastUtils.showShortToast(this@CenterOutSendActivity,currentScanCode)
                    //vibrate Feedback。
                    FeedbackUtils.vibrate(this@CenterOutSendActivity,200)
                    scanNumber=currentScanCode
                    //Sets the content of the searchView.
                    //param submit : Search now(true)
                    searchView?.setQuery( currentScanCode,true)
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
        Log.d(TAG,"获取数据成功!")
        Log.d(TAG,data.toString())
        //******************debugEnd********************
        //Hides the refreshing control.
        refreshLayout.isRefreshing=false
        myToast("获取数据成功!")
        this.data=ArrayList()
        this.data=data
        centerOutSendListViewAdapter.updateData(data)
        lvCenterOutSend.adapter = centerOutSendListViewAdapter
        lvCenterOutSend.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(
                this@CenterOutSendActivity,
                CenterOutSendDetailActivity::class.java
            )
            intent.putExtra("WaybillDetail", data[position])
            startActivity(intent)
        }
    }
    override fun onError(message: String?) {
        //******************debugStart*******************
        Log.d(TAG,"获取数据失败!")
        Log.d(TAG,message)
        //******************debugEnd********************
        refreshLayout.isRefreshing=false
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
