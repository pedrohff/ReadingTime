package com.readingtime.ui.main

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.readingtime.R
import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.extensions.loadPreference
import com.readingtime.model.BookUI
import com.readingtime.ui.booknew.BookNewActivity
import com.readingtime.ui.recording.RecordActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), MainContract.View {
    override fun getViewBookList(): LinkedHashMap<String, BookUI> {
        return bookList
    }

    override fun getViewAdapter(): RecyclerView.Adapter<*>? {
        return rvBookList.adapter
    }

    override fun getViewBinding(): ActivityMainBinding {
        return binding
    }

    lateinit var binding: ActivityMainBinding
    var bookList: LinkedHashMap<String, BookUI> = linkedMapOf()
    lateinit var presenter: MainContract.Presenter
    var highlightedId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        presenter = MainPresenter(this)
        highlightedId = loadPreference(R.string.pref_last_book)
        createButtonListeners()
        createAdapter()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadHighlighted(highlightedId)
        presenter.loadAllBooks()
    }

    override fun createButtonListeners() {
        fabNewBook.setOnClickListener {
            var intent = Intent(this, BookNewActivity::class.java)
            startActivity(intent)
        }

        cvCurrent.setOnClickListener {
            var intent = Intent(this, RecordActivity::class.java)
            intent.putExtra(RecordActivity.BOOK, binding.bpresenter?.book)
            intent.putExtra(RecordActivity.RECORD, binding.bpresenter?.lastRecord)
            startActivity(intent)
        }
    }

    override fun loadHighlightedBookId(): String? {
        return highlightedId
    }

    override fun createAdapter() {
        rvBookList.adapter = MainAdapter(bookList.values, object : MainAdapter.OnClickListener {
            override fun onItemClick(item: BookUI) {
                var intent = Intent(this@MainActivity, RecordActivity::class.java)
                intent.putExtra(RecordActivity.BOOK, item.book)
                intent.putExtra(RecordActivity.RECORD, item.lastRecord)
                this@MainActivity.startActivity(intent)
            }
        })

        rvBookList.layoutManager = LinearLayoutManager(this)
    }
}

