package com.readingtime.ui.main

import android.content.Intent
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.readingtime.ChronometerController
import com.readingtime.R
import com.readingtime.ui.booknew.BookNewActivity
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.menu_player.*
import kotlinx.android.synthetic.main.menu_player.view.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val adapter = MainFragmentAdapter(supportFragmentManager)

        viewPager.adapter = adapter
        adapter.setupPageChangeListener(viewPager, this)
        viewPager.currentItem = 2

    }

    override fun onResume() {
        super.onResume()
        createClickListeners()
        if (ChronometerController.isRunning()) {
            displayCounterMenu()
            ChronometerController.start(chronometerMenu)
        } else {
            hideCounterMenu()
            if (ChronometerController.isPaused()) {
                //TODO: JUST UPDATE THE CHRONOMETER
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (ChronometerController.isRunning()) {
            ChronometerController.storeTimeCounter(chronometerMenu)
        }
    }

    private fun toggleVisibility(imageButton: ImageButton) {
        val visibility = if (imageButton.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
        imageButton.visibility = visibility
        imageButton.isClickable = (imageButton.visibility == View.VISIBLE)
    }

    private fun createClickListeners() {
        ibAdd2.setOnClickListener { onClickAdd() }
        ibBooks2.setOnClickListener { onClickBooks() }
        ibHome2.setOnClickListener { onClickHome() }
        ibStats2.setOnClickListener { onClickStats() }
        ibUser2.setOnClickListener { onClickStats() }

        ibPlay2.setOnClickListener { onClickPlay() }
        ibPause2.setOnClickListener { onClickPause() }
        ibStop2.setOnClickListener { onClickStop() }
    }

    private fun onClickAdd() {
        val intent = Intent(this, BookNewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    private fun onClickBooks() {
//        TODO CREATE BOOK DISPLAY ACTIVITY
//        val intent = Intent(this, ::class.java)
//        startActivity(intent)
    }

    private fun onClickHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)

    }

    private fun onClickStats() {
//        TODO CREATE STATS ACTVITY
//        val intent = Intent(this, ::class.java)
//        startActivity(intent)
    }

    private fun onClickUser() {
//        TODO CREATE USER ACTIVITY
//        val intent = Intent(this, ::class.java)
//        startActivity(intent)
    }

    private fun onClickPlay() {
        ChronometerController.start(chronometerMenu)
    }

    private fun onClickPause() {
        ChronometerController.pause(chronometerMenu)
    }

    private fun onClickStop() {
        ChronometerController.stop(chronometerMenu)
    }

    private fun displayCounterMenu() {
        layoutCounter2.visibility = View.VISIBLE
    }

    private fun hideCounterMenu() {
        layoutCounter2.visibility = View.INVISIBLE
    }
}
