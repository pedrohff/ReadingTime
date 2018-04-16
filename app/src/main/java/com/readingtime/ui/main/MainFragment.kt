package com.readingtime.ui.main

import android.content.Intent
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.firebase.crash.FirebaseCrash
import com.readingtime.R
import com.readingtime.databinding.ActivityMainBinding
import com.readingtime.databinding.FragmentMainBinding
import com.readingtime.extensions.getPercentageColor
import com.readingtime.model.UserBook
import com.readingtime.ui.recording.RecordActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment: Fragment(), MainContract.View {

    lateinit var binding: FragmentMainBinding
    lateinit var presenter: MainContract.Presenter
    private var bookMap: LinkedHashMap<String, UserBook> = linkedMapOf()
    private var bookList: MutableList<UserBook> = mutableListOf()
    private var highlightedId: String? = null
    private lateinit var skeletonCV: SkeletonScreen
    private lateinit var skeletonAdapter: SkeletonScreen


    // Store instance variables
    private var title: String? = null
    private var page: Int = 2

    companion object {
        // newInstance constructor for creating fragment with arguments
        fun newInstance(page: Int, title: String): MainFragment {
            val fragmentFirst = MainFragment()
            val args = Bundle()
            args.putInt("someInt", page)
            args.putString("someTitle", title)
            fragmentFirst.setArguments(args)
            return fragmentFirst
        }
    }

    // Store instance variables based on arguments passed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments!!.getInt("someInt", 2)
        title = arguments!!.getString("someTitle")

        presenter = MainPresenter(this)
    }

    // Inflate the view for the fragment based on layout XML
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
//        val tvLabel = view.findViewById(R.id.tvLabel) as TextView
//        tvLabel.text = page.toString() + " -- " + title


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createButtonListeners()
        createAdapter()
        createSkelleton()
    }

    override fun onResume() {
        super.onResume()
//        highlightedId = loadPreferenceString(Preferences.LAST_BOOK)
        presenter.subscribe(cvCurrent2)
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
        rvBookList2.adapter.notifyDataSetChanged()
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
        Toast.makeText(activity, message, len).show()
    }

    //PRIVATE
    private fun displayCardInfo(bookAux: UserBook) {
        skeletonCV.hide()
        binding.uBookA = bookAux
        updateHighlightedPercentage(bookAux.getPerc())
        ivProgressIcon2?.visibility = View.VISIBLE
        ivHoursIcon2?.visibility = View.VISIBLE
        tvProgressText2?.visibility = View.VISIBLE
        tvTimeRead2?.visibility = View.VISIBLE
    }

    override fun createSkeletonCardView() {
        skeletonCV = Skeleton.bind(cvCurrent2)
                .load(R.layout.sk_main_cardview)
                .duration(1100)
                .angle(0)
                .show()
    }

    private fun createSkelleton() {
        skeletonAdapter = Skeleton.bind(rvBookList2)
                .adapter(rvBookList2.adapter)
                .load(R.layout.sk_main_adapter)
                .duration(1200)
                .angle(0)
                .show()
    }

    private fun createButtonListeners() {
        cvCurrent2.setOnClickListener {
            val intent = Intent(activity, RecordActivity::class.java)
            intent.putExtra(RecordActivity.BOOK, binding.uBookA)
            startActivity(intent)
        }
    }

    private fun createAdapter() {
        rvBookList2.adapter = MainAdapter(bookList, object : MainAdapter.OnClickListener {
            override fun onItemClick(item: UserBook) {
                val intent = Intent(activity, RecordActivity::class.java)
                intent.putExtra(RecordActivity.BOOK, item)
                activity?.startActivity(intent)
            }
        })

        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        rvBookList2.layoutManager = mLayoutManager
    }

    private fun loadHighlightedImage(bookAux: UserBook) {
        val url = bookAux.book.coverURL
        val img = ImageView(activity)
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, Resources.getSystem().displayMetrics).toInt()
        if (url != "") {
            Picasso.with(activity)
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resize(width, height)
                    .centerCrop()
                    .into(img, object : Callback {
                        override fun onSuccess() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                cvCurrent2.background = img.drawable
                            } else {
                                cvCurrent2.setBackgroundDrawable(img.drawable)
                            }
                            displayCardInfo(bookAux)
                        }

                        override fun onError() {
                            Picasso.with(activity)
                                    .load(url)
                                    .resize(width, height)
                                    .centerCrop()
                                    .into(img, object : Callback {
                                        override fun onSuccess() {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                cvCurrent2.background = img.drawable
                                            } else {
                                                cvCurrent2.setBackgroundDrawable(img.drawable)
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
            tvBookperc2?.setTextColor(it)
            tvProgressText2?.setTextColor(it)
            ivProgressIcon2?.setColorFilter(it)
        }
    }
}