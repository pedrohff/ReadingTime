package com.readingtime.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.readingtime.R
import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.extensions.loadPreference
import com.readingtime.model.Book
import com.readingtime.model.BookPresenter
import com.readingtime.model.retrofit.BookApi
import com.readingtime.ui.BookAdapter
import com.readingtime.ui.BookAdapter.OnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class MainActivity : AppCompatActivity() {



    val api = BookApi()
    var currentBook: Book? = null
    lateinit var binding: ActivityMainBinding
    var bookList: MutableList<BookPresenter> = mutableListOf()

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
        setAdapter()

    }

    override fun onResume() {
        super.onResume()
        updateBookList()
        getHighlighted()
    }

    private fun updateBookList() {
        api.listBooks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    presenter -> bookList.add(presenter)
                },{
                    e -> e.printStackTrace()
                },{
                    rvBookList.adapter.notifyDataSetChanged()
                })
    }

    private fun setAdapter() {
        rvBookList.adapter = BookAdapter(bookList, this, object: OnClickListener {
            override fun onItemClick(item: BookPresenter) {
                var intent: Intent = Intent(this@MainActivity, RecordActivity::class.java)
                intent.putExtra("BOOK", item.book)
                intent.putExtra("RECORD", item.lastRecord)
                this@MainActivity.startActivity(intent)
            }
        })

        //Grid Layout: https://medium.com/collabcode/criando-lista-com-recyclerview-no-android-com-kotlin-85cb76f3775d
//        rvBookList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        rvBookList.layoutManager = LinearLayoutManager(this)
    }

    fun getHighlighted() {
        val id = loadPreference(R.string.pref_last_book)
        lateinit var bookAux: BookPresenter
        if (id != null) {
            api.findBook(id).subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({bookPres ->
                        bookAux = bookPres
                    },{e ->
                        e.printStackTrace()
                    },{
                        binding.bpresenter = bookAux
                    })
        }
    }
}

