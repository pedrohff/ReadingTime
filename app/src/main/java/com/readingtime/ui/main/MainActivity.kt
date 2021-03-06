package com.readingtime.ui.main

import android.content.Intent
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.firebase.crash.FirebaseCrash
import com.readingtime.R
import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.extensions.getPercentageColor
import com.readingtime.model.UserBook
import com.readingtime.ui.base.Base
import com.readingtime.ui.booknew.BookNewActivity
import com.readingtime.ui.recording.RecordActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Base(), MainContract.View {
    override lateinit var include: View

    lateinit var binding: ActivityMainBinding
    lateinit var presenter: MainContract.Presenter
    private var bookMap: LinkedHashMap<String, UserBook> = linkedMapOf()
    private var bookList: MutableList<UserBook> = mutableListOf()
    private var highlightedId: String? = null
    private lateinit var skeletonCV: SkeletonScreen
    private lateinit var skeletonAdapter: SkeletonScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        presenter = MainPresenter(this)
//        highlightedId = loadPreferenceString(Preferences.LAST_BOOK)
        createButtonListeners()
        createAdapter()
        createSkelleton()
        include = includeMenu
    }

    override fun onResume() {
        super.onResume()
//        highlightedId = loadPreferenceString(Preferences.LAST_BOOK)
        presenter.subscribe(cvCurrent)
        FirebaseCrash.log("Test")
        Crashlytics.log("Subscribed")
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    //VIEW
    override fun updateAdapter() {
        bookList.clear()
        bookList.addAll(bookMap.values.sortedWith(compareBy({ it.lastVisit })))

        skeletonAdapter.hide()
        rvBookList.adapter.notifyDataSetChanged()
    }

    override fun addBookToAdapter(book: UserBook) {
        bookMap.put(book.id, book)
    }

    override fun addBooksToAdapter(books: List<UserBook>) {
        for (uBook: UserBook in books) {
            bookMap.put(uBook.id, uBook)
        }
    }

    override fun updateHighlighted(bookAux: UserBook) {
        loadHighlightedImage(bookAux)
    }

    override fun makeToast(message: String, len: Int) {
        Toast.makeText(this, message, len).show()
    }

    //PRIVATE
    private fun displayCardInfo(bookAux: UserBook) {
        skeletonCV.hide()
        binding.uBook = bookAux
        updateHighlightedPercentage(bookAux.getPerc())
        ivProgressIcon?.visibility = View.VISIBLE
        ivHoursIcon?.visibility = View.VISIBLE
        tvProgressText?.visibility = View.VISIBLE
        tvTimeRead?.visibility = View.VISIBLE
    }

    override fun createSkeletonCardView() {
        skeletonCV = Skeleton.bind(cvCurrent)
                .load(R.layout.sk_main_cardview)
                .duration(1100)
                .angle(0)
                .show()
    }

    private fun createSkelleton() {
        skeletonAdapter = Skeleton.bind(rvBookList)
                .adapter(rvBookList.adapter)
                .load(R.layout.sk_main_adapter)
                .duration(1200)
                .angle(0)
                .show()
    }

    private fun createButtonListeners() {
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

    private fun loadHighlightedImage(bookAux: UserBook) {
        val url = bookAux.book.coverURL
        val img = ImageView(this)
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, Resources.getSystem().displayMetrics).toInt()
        if (url != "") {
            Picasso.with(this)
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resize(width, height)
                    .centerCrop()
                    .into(img, object : Callback {
                        override fun onSuccess() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                cvCurrent.background = img.drawable
                            } else {
                                cvCurrent.setBackgroundDrawable(img.drawable)
                            }
                            displayCardInfo(bookAux)
                        }

                        override fun onError() {
                            Picasso.with(this@MainActivity)
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
                                            displayCardInfo(bookAux)
                                        }

                                        override fun onError() {

                                        }

                                    })
                        }

                    })
        } else {
            //TODO("PLACEHOLDER IMG")
        }
    }

    private fun updateHighlightedPercentage(percentage: Int) {
        val color = getPercentageColor(percentage)
        color?.let {
            tvBookperc?.setTextColor(it)
            tvProgressText?.setTextColor(it)
            ivProgressIcon?.setColorFilter(it)
        }
    }
}

