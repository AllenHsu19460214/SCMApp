package com.bjjc.scmapp.presenter.interf

import com.bjjc.scmapp.model.bean.CenterOutSendMxBean

/**
 * Created by Allen on 2019/04/22 11:52
 */
interface CenterOutSendRBoxPresenter {
    fun setInitData(datum: CenterOutSendMxBean,outType:String)
    fun enqueueQRCode(scanCodeResult: String?)
    fun startScanQRCodeThread()
}