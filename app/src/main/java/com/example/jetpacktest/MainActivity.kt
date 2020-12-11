package com.example.jetpacktest

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel:MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val countReversed=Utils.readIntFromPre(this,0)
        viewModel=ViewModelProviders.of(this,MainViewModelFactory(countReversed)).get(MainViewModel::class.java)
        btn_plus.setOnClickListener {
            viewModel.plusOne()
            refreshUI()
        }
        refreshUI()
    }

    private fun refreshUI(){
        tv_count.text = viewModel.counter.toString()
    }

    override fun onPause() {
        super.onPause()
        Log.d("kcq","MainActivity-onPause-count:${viewModel.counter}")
        Utils.writeToPre(this, viewModel.counter.value ?: 0)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("kcq","MainActivity-onConfigurationChanged-")
    }
}
