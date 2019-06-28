package com.bjjc.scmapp.util.http_util

import com.bjjc.scmapp.model.bean.CheckQRCodeInfoBean
import com.bjjc.scmapp.model.bean.CommonInfoBean
import com.bjjc.scmapp.model.bean.OutByDistributeDocOrderInfoBean
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Created by Allen on 2018/11/29 14:13
 */
interface ApiService {
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
    ): Call<String>

    @POST("ipJson.php")
    @FormUrlEncoded
    fun getUri(
        @Field("command") command: String
    ): Call<String>

    @POST
    @FormUrlEncoded
    fun centerOutSend(
        @Url url:String,
        @Field("command") command: String,
        @Field("key") key: String?,
        @Field("djtype") djtype: String,
        @Field("crktype") crktype: String,
        @Field("sysIndex") sysIndex: String
    //): Call<OutByDistributeDocInfoBean>
    ): Call<String>

    @POST("storeJson_1.3.0.php")
    @FormUrlEncoded
    fun centerOutSendDetail(
        @Field("command") command: String,
        @Field("key") key: String?,
        @Field("dh") dh: String,
        @Field("smtype") smtype: String
    //): Call<OutByDistributeDocOrderInfoBean>
    ): Call<OutByDistributeDocOrderInfoBean>

    @POST("storeJson_1.3.0.php")
    @FormUrlEncoded
    fun checkQRCode(
        @Field("command") command: String,
        @Field("type") type: String?,
        @Field("sn") sn: String,
        @Field("ckdw") ckdw: String,
        @Field("rkdw") rkdw: String,
        @Field("dh") dh: String
    //): Call<CheckQRCodeInfoBean>
    ): Call<CheckQRCodeInfoBean>

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
    //): Call<CommonInfoBean>
    ): Call<CommonInfoBean>

    @POST("jxJson_1.2.0.php")
    @FormUrlEncoded
    fun centerOutSendDetailWipeCache(
        @Field("command") command: String,
        @Field("dh") dh: String,
        @Field("type") type: String
    //): Call<CommonInfoBean>
    ): Call<CommonInfoBean>
}