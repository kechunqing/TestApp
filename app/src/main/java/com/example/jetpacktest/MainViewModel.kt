package com.example.jetpacktest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by kcq on 2020/11/4
 */
class MainViewModel(countReversed: Int) : ViewModel() {
    var counter = MutableLiveData<Int>()

    init {
        counter.value = countReversed
    }

    fun plusOne() {
        val count = counter.value ?: 0
        counter.value = count + 1
    }

    fun clear() {
        counter.value = 0
    }
}