package com.bjjc.scmapp.util.httpUtils

import com.bjjc.scmapp.model.vo.CenterDistributionOrderOutputDetailVo
import com.bjjc.scmapp.model.vo.CenterDistributionOrderOutputVo
import com.bjjc.scmapp.model.vo.LoginVo
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

    @POST("storeJson_1.3.0.php")
    @FormUrlEncoded
    fun centerDistributionOrderOutput(
        @Field("command") command: String,
        @Field("key") key: String?,
        @Field("djtype") djtype: String,
        @Field("crktype") crktype: String,
        @Field("sysIndex") sysIndex: String
    ): Call<CenterDistributionOrderOutputVo>

    @POST("storeJson_1.3.0.php")
    @FormUrlEncoded
    fun centerDistributionOrderOutput(
        @Field("command") command: String,
        @Field("key") key: String?,
        @Field("dh") djtype: String,
        @Field("smtype") crktype: String
    ): Call<CenterDistributionOrderOutputDetailVo>
}