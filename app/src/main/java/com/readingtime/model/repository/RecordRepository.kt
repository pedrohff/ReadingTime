package com.readingtime.model.repository

import android.util.Log
import com.readingtime.model.Record
import com.readingtime.model.local.LocalProvider
import com.readingtime.model.remote.FirebaseLog
import com.readingtime.model.remote.RemoteBook
import com.readingtime.model.remote.RemoteRecord
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

/**
 * Created by phffeitosa on 20/02/18.
 */
object RecordRepository {

    private val recordApi = RemoteRecord
    private val recordDao = LocalProvider.db.recordDao()

    fun findLast(bookid: String): Single<Record> {
        return Single.concat(
                getLastFromDb(bookid),
                getLastFromApi(bookid)
        ).firstOrError()
    }

    private fun getLastFromDb(bookid: String): Single<Record> {
        return recordDao.last(bookid)
    }

    private fun getLastFromApi(bookid: String): Single<Record>? {
        return recordApi.findLast(bookId=bookid)
                .doOnSuccess { storeRecord(it) }
    }

    private fun storeRecord(record: Record) {
        Observable.fromCallable { recordDao.insert(record) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }

    fun save(record: Record, bookId: String, userId: String = "pedro", onComplete: () -> Unit = {}) {
        recordApi.save(record, bookId, userId, {
            recordDao.insert(record)
            onComplete()
        })

    }
}