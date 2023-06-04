package company.tap.tapuilibraryy.uikit

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.TextView
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew


@SuppressLint("ClickableViewAccessibility")
fun TapTextViewNew.onRightDrawableClicked(onClicked: (view: TextView) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is TextView) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}