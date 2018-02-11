package com.readingtime.model.remote

/**
 * Created by Pedro on 11/02/2018.
 */
class FirebaseLog {

    companion object {
        fun put(message: String) {
            val id = FirebaseProvider.fbRef.push().key
            FirebaseProvider.fbRef.child("log").child(id).setValue(message)
        }
    }
}