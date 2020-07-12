package com.example.testapp.retrofit

import com.example.testapp.utils.KkLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    var retrofit: Retrofit =Retrofit.Builder()
        .baseUrl("http://fanyi.youdao.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build()

    var iHttpRequest: IHttpRequest = retrofit.create(IHttpRequest::class.java)
    var call = iHttpRequest.getCall("")

    fun get(){
        call.enqueue(object :Callback<Reception>{
            override fun onFailure(call: Call<Reception>?, t: Throwable?) {
                KkLog.d("kcq", "ApiService-onFailure-${t?.message}")
            }

            override fun onResponse(call: Call<Reception>?, response: Response<Reception>?) {
                KkLog.d("kcq", "ApiService-onResponse-${response?.message()}-${response?.body()}")
            }

        })
    }

}