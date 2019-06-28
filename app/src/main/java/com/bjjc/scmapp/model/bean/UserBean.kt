package com.bjjc.scmapp.model.bean
import com.bjjc.scmapp.annotation.Poko
import com.google.gson.annotations.SerializedName
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.io.Serializable


/**
 * Created by Allen on 2018/11/29 14:19
 * 用户身份实体类
 */
@Poko
@DatabaseTable(tableName = "t_user")
data class UserBean(
    @DatabaseField(columnName = "id", generatedId = true)
    val id:Int =0,
    /**
     * no using
     *
     * 0
     */
    @DatabaseField(columnName = "ckwwcfd")
    @SerializedName("ckwwcfd")
    val ckwwcfd: Int = 0,
    /**
     * 是否清除缓存标志
     *
     * 0
     */
    @DatabaseField(columnName = "clearbackup")
    @SerializedName("clearbackup")
    val clearbackup: Int = 0,
    /**
     * 公司地址
     *
     * ""
     */
    @DatabaseField(columnName = "gsaddress")
    @SerializedName("gsaddress")
    val gsaddress: String = "",
    /**
     * 公司名称
     *
     * 广州库(成都日鸿)
     */
    @DatabaseField(columnName = "gsmc")
    @SerializedName("gsmc")
    val gsmc: String = "",
    /**
     * 用户权限
     *
     * 出库,入库,货品查询,盘库,货品信息,台帐,分单
     */
    @DatabaseField(columnName = "phone_role_data")
    @SerializedName("phone_role_data")
    val phoneRoleData: String = "",
    /**
     * no using
     *
     * 0
     */
    @DatabaseField(columnName = "rkwwcfd")
    @SerializedName("rkwwcfd")
    val rkwwcfd: Int = 0,
    /**
     * 角色
     *
     * 中心库库管员
     */
    @DatabaseField(columnName = "role")
    @SerializedName("role")
    val role: String = "",
    /**
     * 司机ID
     *
     * ""
     */
    @DatabaseField(columnName = "sjid")
    @SerializedName("sjid")
    val sjid: String = "",
    /**
     * no using
     *
     * 已审通过
     */
    @DatabaseField(columnName = "status")
    @SerializedName("status")
    val status: String = "",
    /**
     * no using
     *
     * 成都日鸿广州仓
     */
    @DatabaseField(columnName = "truename")
    @SerializedName("truename")
    val truename: String = "",
    /**
     * no using
     *
     * GZ-RH
     */
    @DatabaseField(columnName = "username")
    @SerializedName("username")
    val username: String = "",
    /**
     * 用户x坐标信息
     *
     * ""
     */
    @DatabaseField(columnName = "x")
    @SerializedName("x")
    val x: String = "",
    /**
     * 用户y坐标信息
     *
     * ""
     */
    @DatabaseField(columnName = "y")
    @SerializedName("y")
    val y: String = ""
):Serializable