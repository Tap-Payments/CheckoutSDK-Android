package company.tap.tapuilibrary.uikit.utils

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager

/**
 *
 * Created by ola on 25/11/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */

/**
 * Calling this class for adjust view up when keyboard is opened
 *
 * activity?.let { KeyboardUtil(it, view) }
 */



class KeyboardUtil(act: Activity, contentView: View) {
    private val decorView: View = act.window.decorView
    private val contentView: View = contentView

    //a small helper to allow showing the editText focus
    var onGlobalLayoutListener = OnGlobalLayoutListener {
        val r = Rect()
        //r will be populated with the coordinates of your view that area still visible.
        decorView.getWindowVisibleDisplayFrame(r)

        //get screen height and calculate the difference with the useable area from the r
        val height: Int = decorView.getContext().getResources().getDisplayMetrics().heightPixels
        val diff: Int = height - r.bottom

        //if it could be a keyboard add the padding to the view
        if (diff != 0) {
            // if the use-able screen height differs from the total screen height we assume that it shows a keyboard now
            //check if the padding is 0 (if yes set the padding for the keyboard)
            if (contentView.getPaddingBottom() !== diff) {
                //set the padding of the contentView for the keyboard
                contentView.setPadding(0, 0, 0, diff)
            }
        } else {
            //check if the padding is != 0 (if yes reset the padding)
            if (contentView.getPaddingBottom() !== 0) {
                //reset the padding of the contentView
                contentView.setPadding(0, 0, 0, 0)
            }
        }
    }

    fun enable() {
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener)
        }
    }

    fun disable() {
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener)
        }
    }

    companion object {
        /**
         * Helper to hide the keyboard
         *
         * @param act
         */
        fun hideKeyboard(act: Activity?) {
            if (act != null && act.currentFocus != null) {
                val inputMethodManager: InputMethodManager =
                    act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(act.currentFocus!!.windowToken, 0)
            }
        }
    }

    init {

        //only required on newer android versions. it was working on API level 19
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener)
        }
    }
}