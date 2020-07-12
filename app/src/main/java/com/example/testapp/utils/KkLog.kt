package com.example.testapp.utils

import android.util.Log


object KkLog {

    private const val Tag = "Test"

    fun d(tag: String, msg: String) {
        Log.d(tag + Tag, msg)
    }
}