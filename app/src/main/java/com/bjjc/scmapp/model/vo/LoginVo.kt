package com.bjjc.scmapp.model.vo
import com.bjjc.scmapp.annotation.Poko
import com.bjjc.scmapp.model.bean.UserIdentityBean
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by Allen on 2018/11/29 14:15
 * 登录返回信息实体类
 */
@Poko
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
    val sf: UserIdentityBean = UserIdentityBean()
): Serializable

