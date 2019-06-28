package com.bjjc.scmapp.model.bean
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by Allen on 2018/12/21 14:10
 */
data class CheckQRCodeInfoBean(
    @SerializedName("SerialNo")
    val serialNo: String = "",
    @SerializedName("code")
    val code: String = "", // 08
    @SerializedName("errflag")
    val errflag: Boolean = false, // false
    @SerializedName("msg")
    val msg: String = "", // 获取物料编码成功
    @SerializedName("wlbm")
    val wlbm: String = "" // 物料编码 0000002
):Serializable