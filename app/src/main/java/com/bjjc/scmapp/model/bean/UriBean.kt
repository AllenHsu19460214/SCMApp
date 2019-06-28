package com.bjjc.scmapp.model.bean
import com.google.gson.annotations.SerializedName


/**
 * Created by Allen on 2019/06/20 14:11
 */
data class UriBean(
    @SerializedName("ip")
    val ip: String = "", // 222.161.231.93
    @SerializedName("jxJson")
    val jxJson: String = "", // jxJson_1.2.0.php
    @SerializedName("port")
    val port: String = "", // 8080
    @SerializedName("protocol")
    val protocol: String = "", // http
    @SerializedName("storeJson")
    val storeJson: String = "" // storeJson_1.2.0.php
)