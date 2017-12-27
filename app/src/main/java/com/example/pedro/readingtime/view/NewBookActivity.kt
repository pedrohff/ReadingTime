package com.example.pedro.readingtime.view

import android.databinding.DataBindingUtil
import android.databinding.DataBindingUtil.setContentView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.pedro.readingtime.R
import com.example.pedro.readingtime.databinding.ActivityNewBookBinding
import com.example.pedro.readingtime.model.Book
import com.example.pedro.readingtime.model.BookCategory
import com.example.pedro.readingtime.model.BookTypes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_new_book.*

class NewBookActivity() : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var book:Book? = Book()
    var types = arrayOf(BookTypes.BOOK, BookTypes.GRAPHIC_NOVEL)
    var categories = arrayOf(BookCategory.ADVENTURE, BookCategory.FANTASY, BookCategory.SCIENCE_FICTION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)
        val binding: ActivityNewBookBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_book)

        binding.book = Book(name = "asjkdfhsahdf")
        book = binding.book
        initAdapters()

        btSave.setOnClickListener { saveBook() }
    }

    fun initAdapters() {
        spType.setOnItemSelectedListener(this)
        val adapterTypes = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.setAdapter(adapterTypes)

        spCategory.setOnItemSelectedListener(this)
        val adapterCategory = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.setAdapter(adapterCategory)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id) {
            R.id.spCategory -> {

            }
            R.id.spType -> {

            }
        }
    }

    fun saveBook() {
        print(book)
        print("---------------")
        var db :DatabaseReference = FirebaseDatabase.getInstance().getReference("books")
        val key = db.push().key
        book?.id = key
        db.child(key).setValue(book)
    }
}
