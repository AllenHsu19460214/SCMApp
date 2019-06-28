package com.bjjc.scmapp.presenter.`interface`

import android.support.v4.app.FragmentTransaction
import com.bjjc.scmapp.model.bean.OutByDistributionBean

/**
 * Created by Allen on 2019/01/09 9:55
 */
interface IOutByDistributeDocDetailPresenter {
    fun setInitData(datum: OutByDistributionBean)
    fun loadData()
    fun submitOrderInfo()
    fun startScanQRCodeThread()
    fun enqueueQRCode(scanCodeResult: String?)
    fun isOrderFinished():Boolean
    fun setNoCodeToTal(noCodeTotal: Long)
    fun readCache()
    fun transmitDataToDataListFragment()
    fun wipeCacheByOrderId()
    fun wipeCache()
    fun switchFragment(targetFragmentInt: Int): FragmentTransaction
    fun isExistCache():Boolean
}