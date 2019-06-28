package com.bjjc.scmapp.model.bean

import com.bjjc.scmapp.annotation.Poko
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Allen on 2018/12/11 14:30
 * Center Out Send Logistics Documents.
 */
@Poko
data class OutByDistributionBean(
    @SerializedName("允许不扫码入库")
    val 允许不扫码入库: Int = 0, // 0
    @SerializedName("允许不扫码出库")
    val 允许不扫码出库: Int = 0, // 0
    @SerializedName("允许拆单次数")
    val 允许拆单次数: Int = 0, // 0
    @SerializedName("入库单位")
    val 入库单位: String = "", // 一汽-大众备件中转库（长春）,一汽-大众备件中转库（长春）
    @SerializedName("出库单位")
    val 出库单位: String = "", // 广州库(成都日鸿)
    @SerializedName("出库日期")
    val 出库日期: String = "",
    @SerializedName("加急级别")
    val 加急级别: String = "", // 正常
    @SerializedName("单号")
    val 单号: String = "", // WLD2018070509565810501
    @SerializedName("单据状态")
    val 单据状态: String = "", // 已审通过
    @SerializedName("子单数")
    val 子单数: Int = 0, // 0
    @SerializedName("类别")
    val 类别: String = "" // 物流单
): Serializable