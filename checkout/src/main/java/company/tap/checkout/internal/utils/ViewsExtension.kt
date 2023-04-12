package company.tap.checkout.internal.utils

import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.os.postDelayed
import company.tap.checkout.R

private var targetHeight: Int? = 0
private var animationDelayForResizeAnimation = 1500L


fun View.startPoweredByAnimation(delayTime: Long, poweredByLogo: View?) {
    Handler(Looper.getMainLooper()).postDelayed({
        poweredByLogo?.visibility = View.GONE
        this.visibility = View.VISIBLE
        doAftetSpecificTime(execute = {
            poweredByLogo?.visibility = View.VISIBLE
            poweredByLogo?.addFadeInAnimation()
        })
    }, delayTime)
    val resizeAnimation =
        targetHeight?.let {
            ResizeAnimation(
                this,
                it,
                0, true
            )
        }

    resizeAnimation?.duration = animationDelayForResizeAnimation
    this.startAnimation(resizeAnimation)


}

fun doAftetSpecificTime(time: Long = 1000L, execute: () -> Unit) =
    Handler(Looper.getMainLooper()).postDelayed(time) {
        execute.invoke()
    }

fun View.addFadeInAnimation(durationTime: Long = 1000L) {
    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    animation.duration = durationTime
    this.startAnimation(animation)
}

fun adjustHeightAccToDensity(displayMetrics: Int?) {
    if (displayMetrics == DisplayMetrics.DENSITY_260 || displayMetrics == DisplayMetrics.DENSITY_280 || displayMetrics == DisplayMetrics.DENSITY_300 || displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340 || displayMetrics == DisplayMetrics.DENSITY_360) {
        targetHeight = 90
    } else if (displayMetrics == DisplayMetrics.DENSITY_450 || displayMetrics == DisplayMetrics.DENSITY_420 || displayMetrics == DisplayMetrics.DENSITY_400) {
        targetHeight = 120
    } else targetHeight = 140
}


fun createDrawableGradientForBlurry(colorsArrayList: IntArray): GradientDrawable {
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.BL_TR,
        colorsArrayList
    )
    gradientDrawable.gradientRadius = 100f
    gradientDrawable.cornerRadii = floatArrayOf(
        35f, 35f,
        35f, 35f,
        0f, 0f,
        0f, 0f
    )
    return gradientDrawable
}