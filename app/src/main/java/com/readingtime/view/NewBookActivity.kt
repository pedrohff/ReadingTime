package com.readingtime.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.readingtime.R
import com.readingtime.databinding.ActivityNewBookBinding
import com.readingtime.model.Book
import com.readingtime.model.BookCategory
import com.readingtime.model.BookTypes
import kotlinx.android.synthetic.main.activity_new_book.*


class NewBookActivity() : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var book: Book? = Book()
    var types = arrayOf(BookTypes.BOOK, BookTypes.GRAPHIC_NOVEL)
    var categories = arrayOf(BookCategory.ADVENTURE, BookCategory.FANTASY, BookCategory.SCI_FI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)
        val binding: ActivityNewBookBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_book)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        binding.book = Book()
        book = binding.book
        initAdapters()

        btSave.setOnClickListener { saveBook() }
        btCancel.setOnClickListener { finish() }
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
        var db :DatabaseReference = FirebaseDatabase.getInstance().getReference("books")
        val key = db.push().key
        book?.id = key
        db.child(key).setValue(book, DatabaseReference.CompletionListener { databaseError, databaseReference ->  TODO("Verificar")})

        finish()
        TODO("Save an empty record after saving a book")
    }
}
