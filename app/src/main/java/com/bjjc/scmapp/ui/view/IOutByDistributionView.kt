package com.bjjc.scmapp.ui.view

import com.bjjc.scmapp.model.bean.OutByDistributionBean

/**
 * Created by Allen on 2019/01/04 15:34
 */
interface IOutByDistributionView {
    fun onError(message: String?)
    fun onLoadSuccess(data: ArrayList<OutByDistributionBean>)
}