package com.bjjc.scmapp.util.httpUtils

import com.bjjc.scmapp.model.bean.*
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
        @Field("password") password: String?,
        @Field("sbid") sbid: String?,
        @Field("sign") sign: String?,
        @Field("type") type: String,
        @Field("sysIndex") sysIndex: String
    ): Call<LoginBean>

    @POST("storeJson_1.3.0.php")
    @FormUrlEncoded
    fun centerOutSend(
        @Field("command") command: String,
        @Field("key") key: String?,
        @Field("djtype") djtype: String,
        @Field("crktype") crktype: String,
        @Field("sysIndex") sysIndex: String
    ): Call<CenterOutSendBean>

    @POST("storeJson_1.3.0.php")
    @FormUrlEncoded
    fun centerOutSendDetail(
        @Field("command") command: String,
        @Field("key") key: String?,
        @Field("dh") dh: String,
        @Field("smtype") smtype: String
    ): Call<CenterOutSendDetailBean>

    @POST("storeJson_1.3.0.php")
    @FormUrlEncoded
    fun checkQRCode(
        @Field("command") command: String,
        @Field("type") type: String?,
        @Field("sn") sn: String,
        @Field("ckdw") ckdw: String,
        @Field("rkdw") rkdw: String,
        @Field("dh") dh: String
    ): Call<CheckQRCodeBean>

    @POST("storeJson_1.3.0.php")
    @FormUrlEncoded
    fun centerOutSendDetailSaveOrderInfo(
        @Field("command") command: String,
        @Field("key") key: String?,
        @Field("dh") dh: String,
        @Field("zt") zt: String,
        @Field("mx") mx: String,
        @Field("trace") trace: String,
        @Field("point") point: String?,
        @Field("flag") flag: String,
        @Field("isfxdd") isfxdd: String
    ): Call<CommonResultBean>

    @POST("jxJson_1.2.0.php")
    @FormUrlEncoded
    fun centerOutSendDetailWipeCache(
        @Field("command") command: String,
        @Field("dh") dh: String,
        @Field("type") type: String
    ): Call<CommonResultBean>
}