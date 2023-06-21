package company.tap.tapuilibraryy.uikit

import android.annotation.SuppressLint
import android.icu.number.Precision
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.text.TextUtilsCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


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

fun isLayoutRTL () = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
fun String.getColorWithoutOpacity (): String {
    val ifLastColorDigits = this.takeLast(2).isDigitsOnly()
    if (ifLastColorDigits) return this.dropLast(2).toString()
    else return this
}

fun Double.formatTo2DecimalPoints (): String {
    return  String.format("%.2f",this)

}

