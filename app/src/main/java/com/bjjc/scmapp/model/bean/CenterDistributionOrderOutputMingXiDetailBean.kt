package com.bjjc.scmapp.model.bean

import com.bjjc.scmapp.annotation.Poko
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Allen on 2018/12/12 15:56
 */
@Poko
data class CenterDistributionOrderOutputMingXiDetailBean (
    @SerializedName("允许输入箱数")
    val 允许输入箱数: Int = 0, // 0
    @SerializedName("入库单位")
    val 入库单位: String = "", // 一汽-大众备件中转库（广州）
    @SerializedName("入库箱数")
    val 入库箱数: Int = 0, // 0
    @SerializedName("入库输入箱数")
    val 入库输入箱数: Int = 0, // 0
    @SerializedName("出库箱数")
    val 出库箱数: Int = 0, // 0
    @SerializedName("出库输入箱数")
    val 出库输入箱数: Int = 0, // 0
    @SerializedName("原始订单号")
    val 原始订单号: String = "", // B-2906247144-1
    @SerializedName("备件编号")
    val 备件编号: String = "", // LN   052 167 A24
    @SerializedName("提前入库箱数")
    val 提前入库箱数: Int = 0, // 0
    @SerializedName("提前出库箱数")
    val 提前出库箱数: Int = 0, // 0
    @SerializedName("是否允许扫描")
    val 是否允许扫描: Int = 0, // 0
    @SerializedName("物料编码")
    val 物料编码: String = "", // 0000013
    @SerializedName("计划箱数")
    val 计划箱数: Int = 0 // 1
): Serializable