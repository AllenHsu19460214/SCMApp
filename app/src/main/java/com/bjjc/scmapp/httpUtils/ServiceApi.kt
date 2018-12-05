package com.bjjc.scmapp.httpUtils

import com.bjjc.scmapp.login.model.vo.LoginVo
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Allen on 2018/11/29 14:13
 */
interface ServiceApi {
    @POST("authJson.php")
    @FormUrlEncoded
    fun login(
        @Field("command") command: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("sbid") sbid: String,
        @Field("sign") sign: String,
        @Field("type") type: String,
        @Field("sysIndex") sysIndex: String
    ): Call<LoginVo>

}