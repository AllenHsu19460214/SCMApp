package com.bjjc.scmapp.presenter.`interface`

import com.bjjc.scmapp.model.bean.OutByDistributionBean

/**
 * Created by Allen on 2019/04/22 11:52
 */
interface IOutByDistributeDocReducePresenter {
    fun setInitData(datum: OutByDistributionBean, outType:String)
    fun enqueueQRCode(scanCodeResult: String?)
    fun startScanQRCodeThread()
}