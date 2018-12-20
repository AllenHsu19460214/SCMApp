package com.bjjc.scmapp.model.vo
import com.bjjc.scmapp.annotation.Poko
import com.bjjc.scmapp.model.bean.CenterOutSendDetailBean
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by Allen on 2018/12/12 15:54
 */
@Poko
data class CenterOutSendDetailVo(
    @SerializedName("SerialNo")
    val serialNo: String = "",
    @SerializedName("allowyc")
    val allowyc: List<Any> = listOf(),
    @SerializedName("ckdwpoint")
    val ckdwpoint: List<Any> = listOf(),
    @SerializedName("code")
    val code: String = "", // 10
    @SerializedName("msg")
    val msg: String = "", // 返回成功
    @SerializedName("mx")
    val mx: List<CenterOutSendDetailBean> = listOf(),
    @SerializedName("rkdwpoint")
    val rkdwpoint: List<Any> = listOf(),
    @SerializedName("rkxmygpp")
    val rkxmygpp: Int = 0, // 0
    @SerializedName("trace")
    val trace: List<Any> = listOf(),
    @SerializedName("transport")
    val transport: List<Any> = listOf(),
    @SerializedName("仓是否输入")
    var 仓是否输入: Int = 0, // 0
    @SerializedName("修改司机编号标识")
    val 修改司机编号标识: Int = 0, // 0
    @SerializedName("允许分单")
    val 允许分单: Int = 0, // 0
    @SerializedName("允许强制完成")
    val 允许强制完成: Int = 0, // 0
    @SerializedName("允许拆单次数")
    val 允许拆单次数: Int = 0, // 0
    @SerializedName("允许提前发货")
    val 允许提前发货: Int = 0, // 0
    @SerializedName("允许清除缓存")
    val 允许清除缓存: Int = 0, // 0
    @SerializedName("允许输入数量")
    val 允许输入数量: Int = 0, // 0
    @SerializedName("入库单位")
    val 入库单位: String = "", // 一汽-大众备件中转库（广州）,一汽-大众备件中转库（广州）,一汽-大众备件中转库（广州）
    @SerializedName("入库地区")
    val 入库地区: String = "", // 肇庆市
    @SerializedName("入库地点不验证")
    val 入库地点不验证: Int = 0, // 1
    @SerializedName("入库地点判断标识")
    val 入库地点判断标识: Int = 0, // 0
    @SerializedName("出库单位")
    val 出库单位: String = "", // 广州库(成都日鸿)
    @SerializedName("出库地区")
    val 出库地区: String = "", // 广州市
    @SerializedName("出库地点不验证")
    val 出库地点不验证: Int = 0, // 1
    @SerializedName("出库地点判断标识")
    val 出库地点判断标识: Int = 0, // 0
    @SerializedName("出库子单数")
    val 出库子单数: Int = 0, // 0
    @SerializedName("分单全完成")
    val 分单全完成: Int = 0, // 1
    @SerializedName("单号")
    val 单号: String = "", // WLD2018062814022310501
    @SerializedName("单子允许不扫码入库")
    val 单子允许不扫码入库: Int = 0, // 0
    @SerializedName("单子允许不扫码出库")
    val 单子允许不扫码出库: Int = 0, // 0
    @SerializedName("单据状态")
    val 单据状态: String = "", // 已审通过
    @SerializedName("反向订单")
    val 反向订单: Int = 0, // 0
    @SerializedName("司机ID")
    val 司机ID: Any = Any(), // null
    @SerializedName("子单数")
    val 子单数: Int = 0, // 0
    @SerializedName("库允许不扫码入库")
    val 库允许不扫码入库: Int = 0, // 0
    @SerializedName("库允许不扫码出库")
    val 库允许不扫码出库: Int = 0, // 0
    @SerializedName("类别")
    val 类别: String = "", // 物流单
    @SerializedName("解锁标识")
    val 解锁标识: Int = 0, // 0
    @SerializedName("输数入库")
    val 输数入库: Int = 0 // 0
): Serializable
