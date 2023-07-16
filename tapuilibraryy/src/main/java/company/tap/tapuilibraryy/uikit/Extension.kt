package company.tap.tapuilibraryy.uikit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.text.TextUtilsCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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

fun isLayoutRTL() =
    TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL

fun String.getColorWithoutOpacity(): String {
    val ifLastColorDigits = this.takeLast(2).isDigitsOnly()
    val ifLastColorDigit = this.takeLast(1).isDigitsOnly()

    if (ifLastColorDigits || ifLastColorDigit) return this.dropLast(2).toString()

    else return this
}
fun Context.doOnLanguageChange(doOnArabic:()->Unit,doOnEnGlish:()->Unit){
    if (LocalizationManager.getLocale(this).language == "ar") {
       doOnArabic.invoke()
    } else {
        doOnEnGlish.invoke()
    }
}

fun String.formatTo2DecimalPoints(): String {
    return try {
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat("#,###,##0.00",symbols)
        df.roundingMode = RoundingMode.DOWN
       return df.format(this.replace(",", "").toDouble())
    } catch (e: Exception) {
        Log.e("error", this.toString())
        "0"
    }

}

fun Context.doOnChangeOfResolutionDensities(
    onLowDensity: () -> Unit,
    onMediumDensity: () -> Unit,
    onHighDensity: () -> Unit
) {
    val displayMetrics = getDeviceDisplayMetrics(this as Activity)


    if (displayMetrics == DisplayMetrics.DENSITY_260 || displayMetrics == DisplayMetrics.DENSITY_280 || displayMetrics == DisplayMetrics.DENSITY_300 || displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340 || displayMetrics == DisplayMetrics.DENSITY_360) {
        onLowDensity.invoke()
    } else if (displayMetrics == DisplayMetrics.DENSITY_400 ||
        displayMetrics == DisplayMetrics.DENSITY_420 ||
        displayMetrics == DisplayMetrics.DENSITY_440 ||
        displayMetrics == DisplayMetrics.DENSITY_XXHIGH) {
        onMediumDensity.invoke()
    } else {
        onHighDensity.invoke()
    }


}

private fun getDeviceDisplayMetrics(activity: Activity): Int {
    // Determine density
    val metrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(metrics)
    val density = metrics.densityDpi
    return density
}

