package com.bjjc.scmapp.view

import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.bjjc.scmapp.model.bean.CommonResultBean
import com.bjjc.scmapp.model.bean.ExceptionCodeInfoBean

/**
 * Created by Allen on 2019/01/04 15:34
 */
interface CenterOutSendDetailView {
    fun onError(message: String?)
    fun onLoadSuccess(data: CenterOutSendDetailBean)
    fun onSetNoCodeText(noCodeTotal: Long)
    fun onSubmitSuccess(data:CommonResultBean)
    fun updateView(planTotal:Long,scanToTal:Long)
    fun setExceptionTitleColor(exceptionCodeInfoList: ArrayList<ExceptionCodeInfoBean>)
}