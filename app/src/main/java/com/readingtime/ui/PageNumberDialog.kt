package com.readingtime.ui

import android.app.DialogFragment
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.readingtime.R
import kotlinx.android.synthetic.main.dialog_numpage.*
import kotlinx.android.synthetic.main.dialog_numpage.view.*


/**
 * Created by pedro on 28/12/17.
 */
class PageNumberDialog : DialogFragment(), TextView.OnEditorActionListener {


    interface NoticeDialogListener {
        fun onDialogPositiveClick(page: Int)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.dialog_numpage, container)
        dialog.setTitle("Test")

        view.etPageStopped.requestFocus()
        view.etPageStopped.setOnEditorActionListener(this)

        return view
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            val activity = activity as NoticeDialogListener
            activity.onDialogPositiveClick(etPageStopped.text.toString().toInt())
            this.dismiss()
            return true
        }
        return false
    }


}