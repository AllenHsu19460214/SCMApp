package com.bjjc.scmapp.presenter.interf

import com.bjjc.scmapp.model.bean.CenterOutSendBean

/**
 * Created by Allen on 2019/01/09 9:55
 */
interface CenterOutSendDetailPresenter {
    fun loadWaybillDetailData(isRefresh:Boolean,data: CenterOutSendBean)
    fun commitOrSaveOrderInfo2Server(
        b: Boolean,
        data: CenterOutSendBean,
        info: String,
        trace: String,
        point: String?
    )
}