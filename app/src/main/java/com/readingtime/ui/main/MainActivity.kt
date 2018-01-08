package com.readingtime.ui.main

import android.content.Intent
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.widget.ImageView
import com.readingtime.R
import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.extensions.getPercentageColor
import com.readingtime.extensions.loadPreferenceString
import com.readingtime.model.Preferences
import com.readingtime.model.UserBook
import com.readingtime.ui.booknew.BookNewActivity
import com.readingtime.ui.recording.RecordActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), MainContract.View {

    lateinit var binding: ActivityMainBinding
    lateinit var presenter: MainContract.Presenter
    private var bookMap: LinkedHashMap<String, UserBook> = linkedMapOf()
    private var bookList: MutableList<UserBook> = mutableListOf()
    private var highlightedId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        presenter = MainPresenter(this)
        highlightedId = loadPreferenceString(Preferences.LAST_BOOK)
        createButtonListeners()
        createAdapter()
    }

    override fun onResume() {
        super.onResume()
        highlightedId = loadPreferenceString(Preferences.LAST_BOOK)
        presenter.subscribe(highlightedId)
    }

    override fun onStop() {
        super.onStop()
        presenter.unsubscribe()
    }

    //VIEW
    override fun updateAdapter() {
        bookList.clear()
        bookList.addAll(bookMap.values.sortedWith(compareBy({ it.lastVisit })))
        rvBookList.adapter.notifyDataSetChanged()
    }

    override fun addBookToAdapter(book: UserBook) {
        bookMap.put(book.id, book)
    }

    override fun updateHighlighted(bookAux: UserBook) {
        binding.uBook = bookAux
        loadHighlightedImage(bookAux.book.coverURL)
        updateHighlightedPercentage(bookAux.getPerc())
    }

    //PRIVATE
    private fun createButtonListeners() {
        fabNewBook.setOnClickListener {
            val intent = Intent(this, BookNewActivity::class.java)
            startActivity(intent)
        }

        cvCurrent.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            intent.putExtra(RecordActivity.BOOK, binding.uBook)
            startActivity(intent)
        }
    }

    private fun createAdapter() {
        rvBookList.adapter = MainAdapter(bookList, object : MainAdapter.OnClickListener {
            override fun onItemClick(item: UserBook) {
                val intent = Intent(this@MainActivity, RecordActivity::class.java)
                intent.putExtra(RecordActivity.BOOK, item)
                this@MainActivity.startActivity(intent)
            }
        })

        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        rvBookList.layoutManager = mLayoutManager
    }

    private fun loadHighlightedImage(url: String) {
        val img = ImageView(this)
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, Resources.getSystem().displayMetrics).toInt()
        if (url != "") {
            Picasso.with(this)
                    .load(url)
                    .resize(width, height)
                    .centerCrop()
                    .into(img, object : Callback {
                        override fun onSuccess() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                cvCurrent.background = img.drawable
                            } else {
                                cvCurrent.setBackgroundDrawable(img.drawable)
                            }
                        }

                        override fun onError() {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    })
        } else {
            //TODO("PLACEHOLDER IMG")
        }
    }

    private fun updateHighlightedPercentage(percentage: Int) {
        val color = getPercentageColor(percentage)
        tvBookperc.setTextColor(color)
        tvProgressText.setTextColor(color)
        ivProgressIcon.setColorFilter(color)
    }
}

