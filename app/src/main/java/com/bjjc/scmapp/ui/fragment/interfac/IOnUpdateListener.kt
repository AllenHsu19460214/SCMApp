package com.bjjc.scmapp.ui.fragment.interfac

import com.bjjc.scmapp.model.bean.OutByDistributeDocOrderDetailBean

/**
 * Created by Allen on 2018/12/14 9:34
 */
interface IOnUpdateListener {
    fun onUpdate(data:List<OutByDistributeDocOrderDetailBean>)
}