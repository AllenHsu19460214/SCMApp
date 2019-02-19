package com.bjjc.scmapp.view

import com.bjjc.scmapp.model.bean.CommonResultBean
import com.bjjc.scmapp.model.vo.CenterOutSendDetailVo

/**
 * Created by Allen on 2019/01/04 15:34
 */
interface CenterOutSendDetailView {
    fun onError(message: String?)
    fun onLoadWaybillDetailDataSuccess(data: CenterOutSendDetailVo)
    fun onSetNoCodeText(noCodeTotal: Long)
    fun onCommitOrSaveOrderInfoSuccess(data:CommonResultBean)
}