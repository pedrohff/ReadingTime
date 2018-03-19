package com.readingtime.model.remote

import com.readingtime.model.Record
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by pedro on 08/01/18.
 */
object RemoteRecord : RemoteDatabaseHelper() {

    fun save(record: Record, bookId: String, userId: String = "pedro", onComplete: () -> Unit = {}) {
        record.id = FirebaseProvider.fbRef.push().key
        FirebaseProvider
                .fbRef
                .child("records")
                .child(userId)
                .child(bookId)
                .child(record.dateString)
                .setValue(record) { dbError, dbRef ->
                    onComplete()
                }
    }

//    override fun update(classtype: Record, userId: String) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    fun listAll(bookId: String?, userId: String = "pedro"): Observable<Record> {
        return FirebaseProvider.service.recordslist(bookId, userId)
                .flatMap { record -> Observable.fromIterable(record.values) }
    }

    fun findLast(userId: String = "pedro", bookId: String): Maybe<Record> {
        return FirebaseProvider.service.findLastRecord(userId, bookId)
                .map { map -> map.values.first() }
    }

//    override fun findById(typeId: String, userId: String): Observable<Record> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun delete(typeId: String, userId: String) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

}