package com.readingtime.ui.base

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import com.readingtime.R
import com.readingtime.extensions.inflate
import com.readingtime.ui.booknew.BookNewActivity
import com.readingtime.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_base.*

abstract class Base : AppCompatActivity() {

    abstract val layoutR: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        inflate(layoutR)
        createClickListeners()
    }

    on

    private fun inflate(@LayoutRes layoutRes: Int) {
        layoutFill.inflate(layoutRes)
    }

    private fun toggleVisibility(imageButton: ImageButton) {
        val visibility = if (imageButton.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
        imageButton.visibility = visibility
        imageButton.isClickable = (imageButton.visibility == View.VISIBLE)
    }

    fun createClickListeners() {
        ibAdd.setOnClickListener { onClickAdd() }
        ibBooks.setOnClickListener { onClickBooks() }
        ibHome.setOnClickListener { onClickHome() }
        ibStats.setOnClickListener { onClickStats() }
        ibUser.setOnClickListener { onClickStats() }
    }

    private fun onClickAdd() {
        val intent = Intent(this, BookNewActivity::class.java)
        startActivity(intent)
    }

    private fun onClickBooks() {
//        TODO CREATE BOOK DISPLAY ACTIVITY
//        val intent = Intent(this, ::class.java)
        startActivity(intent)
    }

    private fun onClickHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun onClickStats() {
//        TODO CREATE STATS ACTVITY
//        val intent = Intent(this, ::class.java)
        startActivity(intent)
    }

    private fun onClickUser() {
//        TODO CREATE USER ACTIVITY
//        val intent = Intent(this, ::class.java)
        startActivity(intent)
    }

    private fun onClickPlay() {

    }

    private fun onClickPause() {

    }

    private fun onClickStop() {

    }

}
