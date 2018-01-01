package com.readingtime.ui.book

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.readingtime.R
import com.readingtime.model.Book

import kotlinx.android.synthetic.main.activity_book.*

class BookActivity : AppCompatActivity() {

    lateinit var book: Book

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        setSupportActionBar(toolbar)

        book = intent.getParcelableExtra("BOOKACTIVITY_BOOK") //TODO("Use a pattern on parcelable data and put them on strings.xml")

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    fun createAdapter() {
        TODO("Create Adapter")
    }

    fun updateRecords() {
        TODO("Function to retrieve from DB a books records")
    }

}
