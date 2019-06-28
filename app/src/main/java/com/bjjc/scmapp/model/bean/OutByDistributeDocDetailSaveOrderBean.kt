package com.bjjc.scmapp.model.bean
import com.google.gson.annotations.SerializedName


/**
 * Created by Allen on 2019/02/17 10:56
 */
data class OutByDistributeDocDetailSaveOrderBean(
    @SerializedName("原始订单号")
    val 原始订单号: String = "", // TQFH20160503154939379483
    @SerializedName("备件编号")
    val 备件编号: String = "", // G  0020SM44D
    @SerializedName("数量")
    val 数量: Int = 0, // 0
    @SerializedName("物料编码")
    val 物料编码: String = "", // 0000002
    @SerializedName("输入箱数")
    val 输入箱数: Int = 0 // 2
)