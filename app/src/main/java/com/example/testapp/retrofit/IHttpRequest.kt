package com.example.testapp.retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET

interface IHttpRequest {
    @GET("openapi.do?keyfrom=abc&key=2032414398&type=data&doctype=json&version=1.1&q=car")
    fun getCall(@Field("name") name: String): Call<Reception>
}