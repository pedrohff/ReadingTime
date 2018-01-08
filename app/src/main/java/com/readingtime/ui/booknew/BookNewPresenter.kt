package com.readingtime.ui.booknew

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.ImageView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import com.readingtime.ApplicationContextProvider
import com.readingtime.model.Book
import com.readingtime.model.remote.FirebaseProvider
import java.io.IOException

/**
 * Created by pedro on 08/01/18.
 */
class BookNewPresenter(var view: BookNewContract.View) : BookNewContract.Presenter {

    private var api = FirebaseProvider
    private val storageReference = FirebaseStorage.getInstance().getReference("bookCovers/")
    private var bookKey = generateFirebaseKey()
    private val IMG_REQUEST_CODE = 7
    private var selectedImage: ImageView = ImageView(ApplicationContextProvider.context)
    lateinit var filePathUri: Uri


    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveBook(book: Book) {
        api.saveBook(book)
    }

    override fun updateSelectedImage(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMG_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            filePathUri = data.data

            try {

                // Getting selected image into Bitmap.
                val bitmap = MediaStore.Images.Media.getBitmap(view.getResolver(), filePathUri)

                // Setting up bitmap selected image into ImageView.
                selectedImage.setImageBitmap(bitmap)

                view.updateImageTextviews(bitmap.byteCount)
                uploadImage()
            } catch (e: IOException) {

                e.printStackTrace()
            }

        }
    }


    //PRIVATE
    private fun uploadImage() {
        if (filePathUri != null) {
            view.showProgressBar()
            val storageReference2nd = storageReference.child(bookKey + "." + getFileExtension(filePathUri))

            storageReference2nd.putFile(filePathUri)
                    .addOnSuccessListener({ taskSnapshot ->
                        //                        val TempImageName = ImageName.getText().toString().trim()

                        view.hideProgressBar()
                        view.makeToast("Image Uploaded Successfully ") //TODO strings
                        view.setBookCoverUrl(taskSnapshot.downloadUrl!!.toString())

                    })
                    .addOnFailureListener({ exception ->
                        view.hideProgressBar()
                        view.makeToast(exception.message!!)
                    })
                    .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> {
                        //                        progressDialog.setTitle("Image is Uploading...")
                    })
        } else {

            view.makeToast("Please Select Image or Add Image Name") //TODO("Strings")

        }
    }

    private fun generateFirebaseKey(): String? {
        val db = FirebaseDatabase.getInstance()
        return db.getReference("books").push().key
    }

    private fun getFileExtension(uri: Uri): String {

        val contentResolver = view.getResolver()

        val mimeTypeMap = MimeTypeMap.getSingleton()

        return mimeTypeMap.getExtensionFromMimeType(contentResolver?.getType(uri))
    }
}