package com.readingtime.ui.main

import android.content.Intent
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.widget.ImageView
import com.readingtime.R
import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.extensions.getPercentageColor
import com.readingtime.extensions.loadPreference
import com.readingtime.model.BookUI
import com.readingtime.ui.booknew.BookNewActivity
import com.readingtime.ui.recording.RecordActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
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

    override fun loadHighlightedImage(url: String) {
        var img = ImageView(this)
        var width = Resources.getSystem().displayMetrics.widthPixels
        var height: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, Resources.getSystem().displayMetrics).toInt()
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
    }

    override fun updateHighlightedPercentage(percentage: Int) {
        val color = getPercentageColor(percentage)
        tvBookperc.setTextColor(color)
        tvProgressText.setTextColor(color)
        ivProgressIcon.setColorFilter(color)
    }
}

