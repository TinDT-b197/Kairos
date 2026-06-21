package com.example.kairos.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    //private const val BASE_URL = "http://10.0.2.2/kairos_api/"
    private const val BASE_URL = "http://192.168.2.117/kairos_api/"

    val apiService: KairosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KairosApiService::class.java)
    }
}