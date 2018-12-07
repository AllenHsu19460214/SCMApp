package com.bjjc.scmapp.model.bean
import com.bjjc.scmapp.annotation.Poko
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by Allen on 2018/11/29 14:19
 * 用户身份实体类
 */
@Poko
data class SfBean(
    @SerializedName("ckwwcfd")
    val ckwwcfd: Int = 0, // 0  【not to use】
    @SerializedName("clearbackup")
    val clearbackup: Int = 0, // 0  【是否清除缓存标志】
    @SerializedName("gsaddress")
    val gsaddress: Any = Any(), // null  【公司地址】
    @SerializedName("gsmc")
    val gsmc: String = "", // 广州库(成都日鸿) 【公司名称】
    @SerializedName("phone_role_data")
    val phoneRoleData: String = "", // 出库,入库,货品查询,盘库,货品信息,台帐,分单 【用户权限】
    @SerializedName("rkwwcfd")
    val rkwwcfd: Int = 0, // 0   【not to use】
    @SerializedName("role")
    val role: String = "", // 中心库库管员  【角色】
    @SerializedName("sjid")
    val sjid: Any = Any(), // null  【司机ID】
    @SerializedName("status")
    val status: String = "", // 已审通过   【not to use】
    @SerializedName("truename")
    val truename: String = "", // 成都日鸿广州仓   【not to use】
    @SerializedName("username")
    val username: String = "", // GZ-RH     【not to use】
    @SerializedName("x")
    val x: String = "",     //【用户x坐标信息】
    @SerializedName("y")
    val y: Any = Any() // null  【用户y坐标信息】
):Serializable