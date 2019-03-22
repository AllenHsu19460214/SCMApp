package com.bjjc.scmapp.presenter.interf

import com.bjjc.scmapp.model.bean.CenterOutSendMxBean

/**
 * Created by Allen on 2019/01/09 9:55
 */
interface CenterOutSendDetailPresenter {
    fun loadData(data: CenterOutSendMxBean)
    fun submitOrSaveOrderInfo(
        b: Boolean,
        data: CenterOutSendMxBean,
        info: String,
        trace: String,
        point: String?
    )
}