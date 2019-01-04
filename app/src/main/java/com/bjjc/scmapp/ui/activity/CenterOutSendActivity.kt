package com.bjjc.scmapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.barcode.Scanner
import android.os.Handler
import android.os.Message
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import com.bjjc.scmapp.R
import com.bjjc.scmapp.adapter.CenterOutSendListAdapter
import com.bjjc.scmapp.model.bean.CenterOutSendBean
import com.bjjc.scmapp.presenter.impl.CenterOutSendPresenterImpl
import com.bjjc.scmapp.presenter.interf.CenterOutSendPresenter
import com.bjjc.scmapp.ui.activity.base.BaseActivity
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
    }
    override val context: Context by lazy { this }
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val centerOutSendPresenter:CenterOutSendPresenter by lazy { CenterOutSendPresenterImpl(this,this) }
    private var data: List<CenterOutSendBean> = ArrayList()
    private val mHandler = HandheldScanHandler()
    private  var searchView:SearchView?=null
    private val centerOutSendListViewAdapter: CenterOutSendListAdapter by lazy { CenterOutSendListAdapter(this) }

    /**
     * Loads layout of current activity.
     */
    override fun getLayoutId(): Int = R.layout.layout_aty_center_out_send
    override fun initData() {
        initCenterOutSendToolBar()
        centerOutSendPresenter.loadWaybillData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        searchView = setToolBarMenu(true)
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
                    val currentScanCode:String=msg.obj.toString()
                    ToastUtils.showShortToast(this@CenterOutSendActivity,currentScanCode)
                    FeedbackUtils.vibrate(this@CenterOutSendActivity,200)
                    scanNumber=currentScanCode
                    searchView?.setQuery( currentScanCode,false)
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
    override fun loadWaybillDataSuccess(data: List<CenterOutSendBean>) {
        this.data = data
        centerOutSendListViewAdapter.setData(this.data)
        lvCenterOutSend.adapter = centerOutSendListViewAdapter
        lvCenterOutSend.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(
                this@CenterOutSendActivity,
                CenterOutSendDetailActivity::class.java
            )
            intent.putExtra("WaybillDetail", this.data[position])
            startActivity(intent)
        }
    }
    override fun onError(message: String?) {
        message?.let { myToast(message) }
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
}
