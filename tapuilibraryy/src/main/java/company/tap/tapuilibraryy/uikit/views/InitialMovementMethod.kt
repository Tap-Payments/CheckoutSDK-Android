package company.tap.tapuilibraryy.uikit.views

import android.text.Selection
import android.text.Spannable
import android.text.method.MovementMethod
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView

/**
 * Created  on 7/19/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
internal class InitialMovementMethod private constructor() : MovementMethod {
    companion object {
        var sInstance: InitialMovementMethod? = null
        fun getInstance(): MovementMethod {
            if (sInstance == null)
                sInstance =
                    InitialMovementMethod()
            return sInstance as InitialMovementMethod
        }
    }

    private fun DefaultMovementMethod() {}
    override fun initialize(widget: TextView, text: Spannable) {
        // It will mark the IMM as openable
        Selection.setSelection(text, 0)
    }

    override fun onKeyDown(widget: TextView, text: Spannable, keyCode: Int, event: KeyEvent): Boolean {
        return true
    }

    override fun onKeyUp(widget: TextView, text: Spannable, keyCode: Int, event: KeyEvent): Boolean {
        return true
    }

    override fun onKeyOther(view: TextView, text: Spannable, event: KeyEvent): Boolean {
        return true
    }

    override fun onTakeFocus(widget: TextView, text: Spannable, direction: Int) {

    }

    override fun onTrackballEvent(widget: TextView, text: Spannable, event: MotionEvent): Boolean {
        return true
    }

    override fun onTouchEvent(widget: TextView, text: Spannable, event: MotionEvent): Boolean {
        return true
    }

    override fun onGenericMotionEvent(widget: TextView, text: Spannable, event: MotionEvent): Boolean {
        return true
    }

    override fun canSelectArbitrarily(): Boolean {
        return true
    }


}