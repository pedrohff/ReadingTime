package com.readingtime.model.remote

/**
 * Created by pedro on 08/01/18.
 */
open abstract class RemoteDatabaseHelper {
    val service = FirebaseProvider.service
    val reference = FirebaseProvider.fbRef

}