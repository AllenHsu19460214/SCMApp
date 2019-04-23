package com.bjjc.scmapp.model.bean
import com.google.gson.annotations.SerializedName


/**
 * Created by Allen on 2019/02/17 9:45
 */
data class CommonResultBean(
    @SerializedName("SerialNo")
    val serialNo: String = "",
    @SerializedName("code")
    val code: String = "", // 08
    @SerializedName("msg")
    val msg: String = "" // 出库成功
)