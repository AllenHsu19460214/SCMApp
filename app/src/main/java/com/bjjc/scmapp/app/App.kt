package com.bjjc.scmapp.app

import android.app.Application
import com.bjjc.scmapp.model.bean.UriBean
import com.bjjc.scmapp.model.bean.UserBean
import com.bjjc.scmapp.model.entity.DeviceEntity
import com.bjjc.scmapp.model.entity.NetEntity
import com.bjjc.scmapp.model.entity.VersionEntity
import com.bjjc.scmapp.util.SPUtils
import com.bjjc.scmapp.util.UIUtils
import com.hjq.toast.ToastUtils
import com.hjq.toast.style.ToastWhiteStyle

/**
 * Created by Allen on 2018/11/30 14:47
 */
class App : Application() {

    companion object {
        var sUriBean:UriBean?=null
        var sKey: String?=null
        var sUserBean: UserBean? = null
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }
    private fun init(){
        SPUtils.context(this)
        VersionEntity.context(this)
        DeviceEntity.context(this)
        NetEntity.context(this)
        UIUtils.context(this)
        ToastUtils.init(this, ToastWhiteStyle())
    }

}

