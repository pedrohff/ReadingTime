package com.readingtime.ui.booknew

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_new_book.*


class BookNewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, BookNewContract.View {

    private var book: Book? = Book()
    private lateinit var types: MutableMap<Int, String>
    private lateinit var categories: MutableMap<Int, String>
    lateinit var progress: ProgressBar

    lateinit var presenter: BookNewContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)

        types = BookType.getMap(this)
        categories = BookCategory.getMap(this)

        val binding: ActivityNewBookBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_book)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) //TODO review

        presenter = BookNewPresenter(this)
        binding.book = Book()
        book = binding.book

        initAdapters()
        createButtonListeners()
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
        if (book != null && validateBook()) {
            presenter.saveBook(book!!)
            finish()
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

    private fun validateBook(): Boolean {
        var error = ""
        //TODO validate before upload

        if (error != "") {
            makeToast("Fields must be filled: " + error) //TODO strings
            return false
        }
        return true
    }
}
