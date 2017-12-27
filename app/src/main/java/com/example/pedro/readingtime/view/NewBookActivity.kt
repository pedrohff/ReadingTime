package com.example.pedro.readingtime.view

import android.databinding.DataBindingUtil.setContentView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.pedro.readingtime.R

class NewBookActivity : AppCompatActivity(var book:Book) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)
    }
}
