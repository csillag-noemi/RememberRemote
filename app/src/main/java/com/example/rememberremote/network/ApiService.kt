package com.example.rememberremote.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("connected")
    fun sendConnected(@Body message: String): Call<ResponseBody>
}