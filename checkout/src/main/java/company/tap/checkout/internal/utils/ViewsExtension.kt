package company.tap.checkout.internal.utils

import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.core.os.postDelayed
import company.tap.checkout.R
import jp.wasabeef.blurry.Blurry

private var targetHeight: Int? = 0
private var animationDelayForResizeAnimation = 1500L


fun View.startPoweredByAnimation(delayTime: Long, poweredByLogo: View?) {
    Handler(Looper.getMainLooper()).postDelayed({
        poweredByLogo?.visibility = View.GONE
        this.visibility = View.VISIBLE
        doAfterSpecificTime(execute = {
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

fun doAfterSpecificTime(time: Long = 1000L, execute: () -> Unit) =
    Handler(Looper.getMainLooper()).postDelayed(time) {
        execute.invoke()
    }

fun View.addFadeInAnimation(durationTime: Long = 1000L) {
    this.visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    animation.duration = durationTime
    this.startAnimation(animation)
}

fun View.addFadeOutAnimation(durationTime: Long = 500L) {
    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
    animation.duration = durationTime
    this.startAnimation(animation)
    this.animation.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            this@addFadeOutAnimation.visibility = View.GONE
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    })

}

fun MutableList<View>.addFadeOutAnimationToViews(durationTime: Long = 500L) {
    this.forEachIndexed { index, view ->
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)
        animation.duration = durationTime
        view.startAnimation(animation)
        view.animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                view.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
    }


}


fun View.applyBluryToView(radiusNeeded: Int = 8, sampling: Int = 2, animationDuration: Int = 1000) {
    Blurry.with(context).radius(radiusNeeded).sampling(sampling).animate(animationDuration)
        .onto(this as ViewGroup).apply {
            this@applyBluryToView.removeViewAt(0)
        }

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