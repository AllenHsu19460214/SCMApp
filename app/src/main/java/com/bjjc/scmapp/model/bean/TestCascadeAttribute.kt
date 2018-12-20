package com.bjjc.scmapp.model.bean
import com.google.gson.annotations.SerializedName


/**
 * Created by Allen on 2018/12/11 8:57
 */
data class Test2(
    @SerializedName("city")
    val city: City = City(),
    @SerializedName("province")
    val province: List<String> = listOf(),
    @SerializedName("shop")
    val shop: Shop = Shop()
)

data class City(
    @SerializedName("山东省")
    val 山东省: List<String> = listOf(),
    @SerializedName("广东省")
    val 广东省: List<String> = listOf()
)

data class Shop(
    @SerializedName("广州市")
    val 广州市: 广州市 = 广州市(),
    @SerializedName("深圳市")
    val 深圳市: 深圳市 = 深圳市(),
    @SerializedName("韶关市")
    val 韶关市: 韶关市 = 韶关市()
)

data class 韶关市(
    @SerializedName("101")
    val x101: X101 = X101(),
    @SerializedName("102")
    val x102: X102 = X102(),
    @SerializedName("103")
    val x103: X103 = X103()
)

data class X102(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class X101(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class X103(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class 深圳市(
    @SerializedName("110")
    val x110: X110 = X110(),
    @SerializedName("111")
    val x111: X111 = X111(),
    @SerializedName("112")
    val x112: X112 = X112()
)

data class X112(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class X110(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class X111(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class 广州市(
    @SerializedName("104")
    val x104: X104 = X104(),
    @SerializedName("105")
    val x105: X105 = X105(),
    @SerializedName("106")
    val x106: X106 = X106(),
    @SerializedName("107")
    val x107: X107 = X107(),
    @SerializedName("108")
    val x108: X108 = X108(),
    @SerializedName("109")
    val x109: X109 = X109()
)

data class X109(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class X106(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class X105(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class X107(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class X104(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)

data class X108(
    @SerializedName("address")
    val address: String = "", // 韶关市吴江区
    @SerializedName("name")
    val name: String = "" // 韶关大店
)