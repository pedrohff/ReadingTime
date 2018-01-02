package com.readingtime.ui.main

import com.readingtime.model.BookUI
import com.readingtime.model.Record
import com.readingtime.model.remote.FirebaseProvider
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by pedro on 02/01/18.
 */
class MainPresenter(var view: MainContract.View) : MainContract.Presenter {

    val api = FirebaseProvider
    var recordCache = mutableMapOf<String, Record>()

    override fun loadHighlighted(bookId: String?) {
        lateinit var bookAux: BookUI
        if (bookId != null) {
            api.findBook(bookId).subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({ bookPres ->
                        bookAux = bookPres
                    }, { e ->
                        e.printStackTrace()
                    }, {
                        view.getViewBinding().bpresenter = bookAux
                    })
        }
    }

    override fun loadAllBooks() {
        api.listBooks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ presenter ->
                    view.getViewBookList().put(presenter.book!!.id, presenter)
                }, { e ->
                    e.printStackTrace()
                }, {
                    view.getViewAdapter()?.notifyDataSetChanged()
                })
    }

//    private fun getCache(bookId: String): Observable<Record> {
//        return Observable.from(recordCache.keys)
//                .filter{
//                    key -> key == bookId
//                }
//                .map {
//                    key -> boo
//                }
//    }
}