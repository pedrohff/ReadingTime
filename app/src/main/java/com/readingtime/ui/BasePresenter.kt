package com.readingtime.ui

/**
 * Created by pedro on 02/01/18.
 */
interface BasePresenter<V> {
    fun attach(view: V)
    fun detach()
}