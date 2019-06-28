package com.bjjc.scmapp.ui.view

import com.bjjc.scmapp.model.bean.CommonInfoBean
import com.bjjc.scmapp.model.bean.ExceptionQRCodeInfoBean
import com.bjjc.scmapp.model.bean.OutByDistributeDocOrderInfoBean

/**
 * Created by Allen on 2019/01/04 15:34
 */
interface IOutByDistributeDocDetailView {
    fun onError(message: String?)
    fun onLoadSuccess(data: OutByDistributeDocOrderInfoBean)
    fun onSetNoCodeText(noCodeTotal: Long)
    fun onSubmitSuccess(data:CommonInfoBean)
    fun updateView(planTotal:Long,scanToTal:Long)
    fun setExceptionTitleColor(exceptionCodeInfoList: ArrayList<ExceptionQRCodeInfoBean>)
    fun onReturnOutType(outType:String)
}