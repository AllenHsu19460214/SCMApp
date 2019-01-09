package com.bjjc.scmapp.view

import com.bjjc.scmapp.model.vo.CenterOutSendDetailVo

/**
 * Created by Allen on 2019/01/04 15:34
 */
interface CenterOutSendDetailView {
    fun onError(message: String?)
    fun loadWaybillDetailDataSuccess(data: CenterOutSendDetailVo)
    fun setNoCodeText(noCodeTotal: Long)
}