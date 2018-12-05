package com.bjjc.scmapp.login.model.vo
import com.bjjc.scmapp.login.model.bean.SfBean
import com.google.gson.annotations.SerializedName


/**
 * Created by Allen on 2018/11/29 14:15
 * 登录返回信息实体类
 */
data class LoginVo(
    @SerializedName("SerialNo")
    val serialNo: String = "",
    @SerializedName("code")
    val code: String = "", // 08
    @SerializedName("key")
    val key: String = "", // ir/6V2YA3ZRP/10eI8RJZCtvmwy+R5O7YiJBEgIkYbA=
    @SerializedName("msg")
    val msg: String = "", // 登录成功
    @SerializedName("sf")
    val sf: SfBean = SfBean()
)

