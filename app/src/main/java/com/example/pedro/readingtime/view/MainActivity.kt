package com.example.pedro.readingtime.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.pedro.readingtime.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btGo.setOnClickListener {
            var intent:Intent = Intent(this, NewBookActivity::class.java)
            startActivity(intent)
        }
    }
}
