package com.readingtime.ui.main

import com.readingtime.model.UserBook
import com.readingtime.model.remote.FirebaseProvider
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 * Created by pedro on 02/01/18.
 */
class MainPresenter(var view: MainContract.View) : MainContract.Presenter {

    val subscriptions = CompositeSubscription()
    val api = FirebaseProvider

    override fun loadHighlighted(bookId: String?) {
        lateinit var bookAux: UserBook
        if (bookId != null) {
            subscriptions.add(
                    api.findUserBook(bookId = bookId).subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe({ bookPres ->
                                bookAux = bookPres
                            }, { e ->
                                e.printStackTrace()
                            }, {
                                view.updateHighlighted(bookAux)
                            })
            )
        }

    }

    override fun loadAllBooks() {
        subscriptions.add(
                api.listUserBooks()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ uBook ->
                            view.addBookToAdapter(uBook)
                        }, { e ->
                            e.printStackTrace()
                        }, {
                            view.updateAdapter()
                        })
        )
    }

    override fun subscribe(bookId: String?) {
        loadHighlighted(bookId)
        loadAllBooks()
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

}