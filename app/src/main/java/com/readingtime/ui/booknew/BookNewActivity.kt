package com.readingtime.ui.booknew

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import com.readingtime.R
import com.readingtime.databinding.ActivityNewBookBinding
import com.readingtime.model.Book
import com.readingtime.model.BookCategory
import com.readingtime.model.BookTypes
import com.readingtime.model.remote.FirebaseProvider
import kotlinx.android.synthetic.main.activity_new_book.*
import java.io.IOException






class BookNewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var api = FirebaseProvider
    var book: Book? = Book()
    var types = arrayOf(BookTypes.BOOK, BookTypes.GRAPHIC_NOVEL)
    var categories = arrayOf(BookCategory.ADVENTURE, BookCategory.FANTASY, BookCategory.SCI_FI)

    //Uploading
    val IMG_REQUEST_CODE = 7
    lateinit var filePathUri: Uri
    lateinit var selectedImage: ImageView
    lateinit var progress: ProgressBar
    val storageReference = FirebaseStorage.getInstance().getReference("bookCovers/")
    var bookKey = generateFirebaseKey()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)
        val binding: ActivityNewBookBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_book)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        binding.book = Book()
        book = binding.book
        initAdapters()

        selectedImage = ImageView(this)

        btSave.setOnClickListener { saveBook() }
        btCancel.setOnClickListener { finish() }
        btUpload.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Please select the uBook cover"), 7) //TODO("Strings")
        }
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
            Log.e("NPE", "Trying to save null uBook")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMG_REQUEST_CODE && resultCode === Activity.RESULT_OK && data != null && data.data != null) {

            filePathUri = data.data

            try {

                // Getting selected image into Bitmap.
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePathUri)

                // Setting up bitmap selected image into ImageView.
                selectedImage.setImageBitmap(bitmap)

                // After selecting image change choose button above text.
                btUpload.text = "Image Selected" //TODO("Strings")

                val mb = (bitmap.byteCount / 1048576).toString()
                tvFilesize.text = "File size: " + bitmap.byteCount

                uploadImageFileToFirebaseStorage()
            } catch (e: IOException) {

                e.printStackTrace()
            }

        }
    }

    fun getFileExtension(uri: Uri): String {

        val contentResolver = contentResolver

        val mimeTypeMap = MimeTypeMap.getSingleton()

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    fun uploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (filePathUri != null) {

            // Setting progressDialog Title.
            progress = ProgressBar(this, null, R.attr.progressBarStyle)

            // Showing progressDialog.
            progress.visibility = View.VISIBLE

            // Creating second StorageReference.
            val storageReference2nd = storageReference.child(bookKey + "." + getFileExtension(filePathUri))

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(filePathUri)
                    .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                        // Getting image name from EditText and store into string variable.
//                        val TempImageName = ImageName.getText().toString().trim()

                        // Hiding the progressDialog after done uploading.
                        progress.visibility = View.GONE

                        // Showing toast message after done uploading.
                        Toast.makeText(applicationContext, "Image Uploaded Successfully ", Toast.LENGTH_LONG).show() //TODO("Strings")

//                        val imageUploadInfo = ImageUploadInfo(TempImageName, )

                        // Getting image upload ID.
//                        val ImageUploadId = databaseReference.push().getKey()

                        // Adding image upload id s child element into databaseReference.
//                        databaseReference.child(ImageUploadId).setValue(imageUploadInfo)
                        book?.coverURL = taskSnapshot.downloadUrl!!.toString()
                    })
                    // If something goes wrong .
                    .addOnFailureListener(OnFailureListener { exception ->
                        // Hiding the progressDialog.
                        progress.visibility = View.GONE

                        // Showing exception erro message.
                        Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                    })

                    // On progress change upload time.
                    .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> {
                        // Setting progressDialog Title.
//                        progressDialog.setTitle("Image is Uploading...")
                    })
        } else {

            Toast.makeText(this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show() //TODO("Strings")

        }
    }

    fun generateFirebaseKey(): String? {
        val db = FirebaseDatabase.getInstance()
        return db.getReference("books").push().key
    }
}
