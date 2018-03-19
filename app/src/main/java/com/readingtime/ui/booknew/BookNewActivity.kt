package com.readingtime.ui.booknew

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import com.readingtime.R
import com.readingtime.databinding.ActivityNewBookBinding
import com.readingtime.model.Book
import com.readingtime.model.BookCategory
import com.readingtime.model.BookType
import com.readingtime.ui.base.Base
import kotlinx.android.synthetic.main.activity_new_book.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.util.TypedValue


class BookNewActivity : Base(), AdapterView.OnItemSelectedListener, BookNewContract.View {

    override lateinit var include: View


    private var book: Book? = Book()
    private lateinit var types: MutableMap<Int, String>
    private lateinit var categories: MutableMap<Int, String>
    lateinit var progress: ProgressBar

    lateinit var presenter: BookNewContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)

//        layoutInflater.inflate(R.layout.menu_player, menu)

//        val layout: ConstraintLayout = findViewById(R.id.menuplayertest)
//        menu.addView(layout)
        types = BookType.getMap(this)
        categories = BookCategory.getMap(this)

        val binding: ActivityNewBookBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_book)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) //TODO review

        presenter = BookNewPresenter(this)
        binding.book = Book()
        book = binding.book

        initAdapters()
        createButtonListeners()
//        testFillBook

        test()
        include = includeMenu
    }

    private fun test () {

        val activityRootView = outercl
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight()
                println("a" + activityRootView.getRootView().getHeight())
                println("b" + activityRootView.getHeight())
                println("c" + heightDiff)
                if (heightDiff > dpToPx(this@BookNewActivity, 200f)) { // if more than 200 dp, it's probably a keyboard...
//                    scrollView.layoutParams.height = (0.75 * scrollView.height).toInt()
                    println(" 111")
                    includeMenu.visibility = View.GONE
                } else {
//                    scrollView.layoutParams.height = activityRootView.getRootView().getHeight()
                    println(" 222")
                    includeMenu.visibility = View.VISIBLE
                }
            }
        })
    }

    fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }


    private fun testFillBook() {
        book?.let {
            it.name = "Moebius Library: The World of Edena"
            it.pages = 360
            etPages.setText(360.toString())
            it.publisher = "Dark Horse Books"
            it.coverURL = "https://firebasestorage.googleapis.com/v0/b/pizzagent-e12b9.appspot.com/o/bookCovers%2F-L3or1kmkz0JbPq79lJu.jpg?alt=media&token=5409951e-c24c-46f5-824d-cbe67192e225"
            it.category = "SCI-FI"
            it.author = "Moebius"
            it.artist = "Moebius"
            it.type = "GRAPHIC_NOVEL"
        }
    }

    private fun initAdapters() {
        spType.onItemSelectedListener = this
        val adapterTypes = ArrayAdapter(this, R.layout.spinner_item, types.values.toList())
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.adapter = adapterTypes

        spCategory.onItemSelectedListener = this
        val adapterCategory = ArrayAdapter(this, R.layout.spinner_item, categories.values.toList()) //TODO order by name
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapterCategory
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id) {
            R.id.spCategory -> {
                var selected: String? = parent.getItemAtPosition(position) as? String
                book?.category = BookCategory.getEnumFromValue(categories, selected!!).toString()
                //TODO try/catch nullpointer
            }
            R.id.spType -> {
                var selected: String? = parent.getItemAtPosition(position) as? String
                book?.type = BookType.getEnumFromValue(types, selected!!).toString()
                //TODO try/catch nullpointer
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.updateSelectedImage(requestCode, resultCode, data, this)
    }


    //VIEW
    override fun updateImageTextviews(string: String) {
        btUpload.text = "Image Selected" //TODO("Strings")

        tvFilesize.text = "File size: " + string //TODO strings
    }

    override fun showProgressBar() {
        progress = ProgressBar(this, null, R.attr.progressBarStyle)
        progress.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    override fun makeToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    override fun setBookCoverUrl(url: String) {
        book?.coverURL = url
    }

    //PRIVATE
    private fun saveBook() {

        book?.pages = etPages.text.toString().toInt()
        if (book != null) {
            presenter.saveBook(book!!, {
                finish()
            })
        } else {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            Log.e("NPE", "Trying to save null uBook")
        }
    }

    private fun createButtonListeners() {
        btSave.setOnClickListener { saveBook() }
        btCancel.setOnClickListener { finish() }
        btUpload.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Please select the Book cover"), 7) //TODO("Strings")
        }
    }
}
