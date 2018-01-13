package com.readingtime.ui.booknew

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import com.readingtime.ApplicationContextProvider
import com.readingtime.extensions.FileUtil
import com.readingtime.model.Book
import com.readingtime.model.remote.RemoteBook
import id.zelory.compressor.Compressor
import java.io.File
import java.io.IOException
import java.text.DecimalFormat

/**
 * Created by pedro on 08/01/18.
 */
class BookNewPresenter(var view: BookNewContract.View) : BookNewContract.Presenter {

    private val storageReference = FirebaseStorage.getInstance().getReference("bookCovers/")
    private var bookKey = generateFirebaseKey()
    private val IMG_REQUEST_CODE = 7
    private var selectedImage: ImageView = ImageView(ApplicationContextProvider.context)
    lateinit var filePathUri: Uri
    private lateinit var imageFile: File


    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveBook(book: Book, onComplete: () -> Unit) {
        try {
            RemoteBook.save(book, provKey = bookKey, onComplete = onComplete)
        } catch (exc: IllegalArgumentException) {
            view.makeToast(exc.message.toString())
        }
    }

    override fun updateSelectedImage(requestCode: Int, resultCode: Int, data: Intent?, context: Context) {
        if (requestCode == IMG_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            filePathUri = data.data

            try {
                imageFile = FileUtil.from(context, data.data)
                // Getting selected image into Bitmap.
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, filePathUri)
                // Setting up bitmap selected image into ImageView.
                selectedImage.setImageBitmap(bitmap)

                if (imageFile.length() / 1024 > 200) {
                    compressImage(imageFile, context)
                }

                view.updateImageTextviews(getReadableFileSize(imageFile.length()))
                uploadImage(context.contentResolver)

            } catch (e: IOException) {

                e.printStackTrace()
            }
        }
    }


    //PRIVATE
    private fun compressImage(file: File, context: Context) {
        imageFile = Compressor(context).compressToFile(file)
        filePathUri = Uri.fromFile(imageFile)
    }

    private fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    private fun uploadImage(contentResolver: ContentResolver) {
        if (filePathUri != null) {
            view.showProgressBar()
            val storageReference2nd = storageReference.child(bookKey + "." + getFileExtension(filePathUri, contentResolver))

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

    private fun getFileExtension(uri: Uri, contentResolver: ContentResolver): String {

        var string = uri.toString()
        string = string.substring(string.lastIndexOf(".") + 1)
        return string
    }
}