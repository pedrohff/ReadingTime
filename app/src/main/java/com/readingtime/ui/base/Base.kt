package com.readingtime.ui.base

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.View
import android.widget.ImageButton
import com.readingtime.ChronometerController
import com.readingtime.model.UserBook
import com.readingtime.ui.booknew.BookNewActivity
import com.readingtime.ui.main.MainActivity
import kotlinx.android.synthetic.main.menu_player.*
import kotlinx.android.synthetic.main.menu_player.view.*

abstract class Base : AppCompatActivity() {

    abstract val layoutR: Int
    abstract var include: View
    lateinit var currentBook: UserBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        createClickListeners()
        if (ChronometerController.isRunning()) {
            ChronometerController.start(currentBook.id, chronometer)
        } else {
            if (ChronometerController.isPaused()) {
                //TODO: JUST UPDATE THE CHRONOMETER
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (ChronometerController.isRunning()) {
            ChronometerController.storeTimeCounter(currentBook.id, chronometer)
        }
    }

    private fun inflate(@LayoutRes layoutRes: Int) {
//        layoutFill.inflate(layoutRes)
//        val layout = layoutInflater.inflate(layoutRes, layoutFill)
//        layoutFill.addView(layout)
    }

    private fun toggleVisibility(imageButton: ImageButton) {
        val visibility = if (imageButton.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
        imageButton.visibility = visibility
        imageButton.isClickable = (imageButton.visibility == View.VISIBLE)
    }

    fun createClickListeners() {
        include.ibAdd.setOnClickListener { onClickAdd() }
        include.ibBooks.setOnClickListener { onClickBooks() }
        include.ibHome.setOnClickListener { onClickHome() }
        include.ibStats.setOnClickListener { onClickStats() }
        include.ibUser.setOnClickListener { onClickStats() }

        include.ibPlay.setOnClickListener { onClickPlay() }
        include.ibPause.setOnClickListener { onClickPause() }
        include.ibStop.setOnClickListener { onClickStop() }
    }

    private fun onClickAdd() {
        val intent = Intent(this, BookNewActivity::class.java)
        startActivity(intent)
    }

    private fun onClickBooks() {
//        TODO CREATE BOOK DISPLAY ACTIVITY
//        val intent = Intent(this, ::class.java)
//        startActivity(intent)
    }

    private fun onClickHome() {
        val intent = Intent(this, MainActivity::class.java)
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
        ChronometerController.start(currentBook.id, chronometer)
    }

    private fun onClickPause() {
        ChronometerController.pause(currentBook.id, chronometer)
    }

    private fun onClickStop() {
        ChronometerController.stop(currentBook.id, chronometer)
    }

}
