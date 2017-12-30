package com.readingtime.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.readingtime.R
import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.model.Book
import com.readingtime.model.BookPresenter
import com.readingtime.model.Record
import com.readingtime.model.retrofit.BookApi
import kotlinx.android.synthetic.main.activity_main.*
import mu.KLogging
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.IllegalStateException


class MainActivity : AppCompatActivity() {


    companion object: KLogging()

    val api = BookApi()
    var currentBook: Book? = null
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        fabNewBook.setOnClickListener {
            var intent:Intent = Intent(this, NewBookActivity::class.java)
            startActivity(intent)
        }


        cvCurrent.setOnClickListener {
            var intent:Intent = Intent(this, RecordActivity::class.java)
            intent.putExtra("BOOK", binding.bpresenter?.book)
            intent.putExtra("RECORD", binding.bpresenter?.lastRecord)
            startActivity(intent)
        }

        updateCurrentBook()
    }

    private fun updateCurrentBook(){
        getCurrentBook()
    }

    private fun getCurrentBook() {
        var auxBook: Book? = null
        api.loadBooks()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({book ->
                    auxBook = book
                },{e ->
                    e.printStackTrace()
                },{
                    getRecords(auxBook)
                })
    }

    private fun getRecords(book:Book?) {
        var records = mutableListOf<Record>()
        api.listRecords(book?.id)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    record -> records.add(record)
                },{
                    e-> if(e.equals(IllegalStateException::class.java)) {
                        binding.bpresenter = BookPresenter.construct(book, null)
                        logger("eeeeeeeeeeeee")
                    }
                },{
                    binding.bpresenter = BookPresenter.construct(book, records)
                })
    }
}

