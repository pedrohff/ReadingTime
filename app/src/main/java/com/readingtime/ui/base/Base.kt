package com.readingtime.ui.base

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.readingtime.R

class Base : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
}
