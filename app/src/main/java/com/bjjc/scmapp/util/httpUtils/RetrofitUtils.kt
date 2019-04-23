package com.bjjc.scmapp.util.httpUtils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Allen on 2018/11/29 15:18
 */
class RetrofitUtils {
    companion object {
        private val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        //private val BASE_URL = App.getDevModelValue("BASE_URL")
        fun getRetrofit(base_url: String): Retrofit =
            Retrofit.Builder()
                .baseUrl(base_url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    }
}