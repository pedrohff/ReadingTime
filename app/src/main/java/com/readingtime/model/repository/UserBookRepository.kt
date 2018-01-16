package com.readingtime.model.repository

import com.readingtime.model.UserBook
import com.readingtime.model.local.LocalProvider
import com.readingtime.model.remote.RemoteUserBook
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * Created by pedro on 15/01/18.
 */
object UserBookRepository {
    val userBookDao = LocalProvider.db.userBookDao()
    val userBookApi = RemoteUserBook


    fun getUserBooks(userId: String): Observable<List<UserBook>> {
        return Observable.concatArray(
                getUserbooksFromDb(),
                getUserbooksFromApi(userId)
        )
    }

    private fun getUserbooksFromDb(): Observable<List<UserBook>> {
        return userBookDao.last().toObservable()
    }

    private fun getUserbooksFromApi(userId: String): Observable<MutableList<UserBook>> {
        return userBookApi
                .listAllNew(userId)
                .doOnNext { storeUserBook(it) }
    }

    private fun storeUserBook(userBooks: List<UserBook>) {
        Observable.fromCallable { userBookDao.insertAll(userBooks) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }
}