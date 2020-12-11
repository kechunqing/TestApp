package com.example.jetpacktest

import android.content.Context

/**
 * Created by kcq on 2020/11/6
 */
object Utils {
    fun writeToPre(context: Context,count:Int) {
        val sp = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        sp.edit().putInt("count_reversed",count).apply()
    }

    fun readIntFromPre(context: Context, defaultValue:Int):Int {
        val sp=context.getSharedPreferences("data",Context.MODE_PRIVATE)
        return sp.getInt("count_reversed",defaultValue)
    }
}