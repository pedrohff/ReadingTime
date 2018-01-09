package com.readingtime.model.remote

import com.readingtime.model.Record
import rx.Observable

/**
 * Created by pedro on 08/01/18.
 */
object RemoteRecord : RemoteDatabaseHelper() {

    fun save(record: Record, bookId: String, userId: String = "pedro") {
        FirebaseProvider.fbRef.child("records").child(userId).child(bookId).child(record.id).setValue(record)
    }

//    override fun update(classtype: Record, userId: String) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    fun listAll(bookId: String?, userId: String = "pedro"): Observable<Record> {
        return FirebaseProvider.service.recordslist(bookId, userId)
                .flatMap { record -> Observable.from(record.values) }
    }

    fun findLast(userId: String = "pedro", bookId: String): Observable<Record> {
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