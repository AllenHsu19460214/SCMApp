package com.bjjc.scmapp.view

import com.bjjc.scmapp.model.bean.CenterOutSendBean

/**
 * Created by Allen on 2019/01/04 15:34
 */
interface CenterOutSendView {
    fun onError(message: String?)
    fun loadWaybillDataSuccess(data: List<CenterOutSendBean>)
}