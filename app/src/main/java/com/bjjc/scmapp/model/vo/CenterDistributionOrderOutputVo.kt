package com.bjjc.scmapp.model.vo
import com.bjjc.scmapp.annotation.Poko
import com.bjjc.scmapp.model.bean.CenterDistributionOrderOutputMingXiBean
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by Allen on 2018/12/11 14:24
 */
@Poko
data class CenterDistributionOrderOutputVo(
    @SerializedName("SerialNo")
    val serialNo: String = "",
    @SerializedName("code")
    val code: String = "", // 10
    @SerializedName("msg")
    val msg: String = "", // 返回成功
    @SerializedName("mx")
    val mx: List<CenterDistributionOrderOutputMingXiBean> = listOf()
): Serializable
