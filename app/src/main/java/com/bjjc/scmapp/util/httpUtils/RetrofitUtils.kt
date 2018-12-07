package com.bjjc.scmapp.util.httpUtils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Allen on 2018/11/29 15:18
 */
class RetrofitUtils {
    companion object {
        //private val BASE_URL = App.getDevModelValue("BASE_URL")
        fun getRetrofit(base_url:String): Retrofit =
            Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    }
}