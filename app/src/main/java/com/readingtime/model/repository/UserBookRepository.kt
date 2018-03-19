package com.readingtime.model.repository

import com.readingtime.model.UserBook
import com.readingtime.model.local.LocalProvider
import com.readingtime.model.remote.FirebaseLog
import com.readingtime.model.remote.FirebaseProvider
import com.readingtime.model.remote.FirebaseService
import com.readingtime.model.remote.RemoteUserBook
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by pedro on 15/01/18.
 */
object UserBookRepository {
    val userBookDao = LocalProvider.db.userBookDao()
    val userBookApi = RemoteUserBook

    fun update(userBook: UserBook, onComplete: () -> Unit = {}) {
        userBookApi.update(userBook, onComplete = {
            Observable.fromCallable { userBookDao.update(userBook) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(onComplete)
                    .subscribe()
        })
    }


    fun getUserBooks(userId: String): Observable<List<UserBook>> {
        return Observable.concatArrayEager(
                getUserbooksFromDb(),
                getUserbooksFromApi(userId)
        )
    }

    fun getLastUserbook(userId: String = "pedro"): Single<UserBook> {
        return userBookDao.last()
    }

    private fun getUserbooksFromDb(): Observable<List<UserBook>> {
        return userBookDao.recent()
                .toObservable()
                .debounce(400, TimeUnit.MILLISECONDS)
                .doOnNext {
            FirebaseLog.put("Fetching ${it.size} userbooks from DB. Date: ${Date().toString()}")
        }
    }

    private fun getUserbooksFromApi(userId: String): Observable<MutableList<UserBook>> {
        return userBookApi
                .listAllNew(userId)
                .doOnNext {
                    storeUserBook(it)
                    FirebaseLog.put("Fetching ${it.size} userbooks from API. Date: ${Date().toString()}")
                }
                .onErrorReturn {
                    emptyList<UserBook>().toMutableList()
                }
    }

    private fun storeUserBook(userBooks: List<UserBook>) {
        Observable.fromCallable { userBookDao.insertAll(userBooks) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }

    fun save(userBook: UserBook, userId: String = "pedro", onComplete: () -> Unit = {}) {
        RemoteUserBook.save(userBook, userId, {
            storeUserBook(listOf(userBook))
            onComplete()
        })

    }
}