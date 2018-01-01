package com.readingtime.ui.booknew

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.readingtime.R
import com.readingtime.databinding.ActivityNewBookBinding
import com.readingtime.model.Book
import com.readingtime.model.BookCategory
import com.readingtime.model.BookTypes
import com.readingtime.model.remote.FirebaseProvider
import kotlinx.android.synthetic.main.activity_new_book.*


class BookNewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var api = FirebaseProvider
    var book: Book? = Book()
    var types = arrayOf(BookTypes.BOOK, BookTypes.GRAPHIC_NOVEL)
    var categories = arrayOf(BookCategory.ADVENTURE, BookCategory.FANTASY, BookCategory.SCI_FI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)
        val binding: ActivityNewBookBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_book)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        binding.book = Book()
        book = binding.book
        initAdapters()

        btSave.setOnClickListener { saveBook() }
        btCancel.setOnClickListener { finish() }
    }

    fun initAdapters() {
        spType.onItemSelectedListener = this
        val adapterTypes = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.adapter = adapterTypes

        spCategory.onItemSelectedListener = this
        val adapterCategory = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapterCategory
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id) {
            R.id.spCategory -> {
                var selected: BookCategory?= parent.getItemAtPosition(position) as? BookCategory
                book?.category = selected?.cat.toString()
            }
            R.id.spType -> {
                var selected: BookTypes? = parent.getItemAtPosition(position) as? BookTypes
                book?.type = selected?.type.toString()
            }
        }
    }

    fun saveBook() {
        if (book != null) {
            api.saveBook(book!!)
            finish()
        } else {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            Log.e("NPE", "Trying to save null book")
        }
    }
}
