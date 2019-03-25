package com.bjjc.scmapp.presenter.interf

import android.support.v4.app.FragmentTransaction
import com.bjjc.scmapp.model.bean.CenterOutSendMxBean

/**
 * Created by Allen on 2019/01/09 9:55
 */
interface CenterOutSendDetailPresenter {
    fun setInitData(datum: CenterOutSendMxBean)
    fun loadData()
    fun submitOrSaveOrderInfo()
    fun startScanQRCodeThread()
    fun enqueueQRCode(scanCodeResult: String?)
    fun isOrderFinished():Boolean
    fun setNoCodeToTal(noCodeTotal: Long)
    fun readCache()
    fun transmitDataToDataListFragment()
    fun wipeCacheByOrder()
    fun wipeCache()
    fun switchFragment(targetFragmentInt: Int): FragmentTransaction
}