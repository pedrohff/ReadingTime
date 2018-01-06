package com.readingtime.ui.recording

import android.app.DialogFragment
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
        val imm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        } else {
            null
        }
        val view = inflater!!.inflate(R.layout.dialog_numpage, container)

        view.etPageStopped.requestFocus()
        view.etPageStopped.postDelayed(object : Runnable {
            override fun run() {
                if (imm is InputMethodManager) {
                    imm.showSoftInput(view.etPageStopped, InputMethodManager.SHOW_IMPLICIT)
                    view.etPageStopped.setOnEditorActionListener(this@PageNumberDialog)
                }
            }

        }, 300)

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