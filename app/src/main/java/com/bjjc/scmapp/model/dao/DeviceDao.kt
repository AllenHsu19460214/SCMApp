package com.bjjc.scmapp.model.dao

import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.model.bean.DeviceBean
import com.bjjc.scmapp.model.dao.`interface`.IDeviceDao
import com.bjjc.scmapp.util.GetSign
import com.bjjc.scmapp.util.MobileInfoUtil
import com.bjjc.scmapp.util.ResourcePathUtils
import com.bjjc.scmapp.util.UIUtils

/**
 * Created by Allen on 2019/04/11 11:08
 */
class DeviceDao :IDeviceDao <DeviceBean>{
    private val deviceBean by lazy { DeviceBean() }
    override fun getDevice(): DeviceBean {
        //Gets the phone IMEI.
        if(App.isPDA){
            deviceBean.imei= MobileInfoUtil.getIMEI(UIUtils.getContext())
        }else{
            //This is imei of handheld.
            deviceBean.imei= "355128005784806"
        }
        //deviceBean.imei= "862460034821507"
        //Get the Key form SD card.
        deviceBean.sign= GetSign.readTxtFile(ResourcePathUtils.getSDRootPath(), "${UIUtils.getContext().packageName}/Key/sign.key")
        return deviceBean
    }
}