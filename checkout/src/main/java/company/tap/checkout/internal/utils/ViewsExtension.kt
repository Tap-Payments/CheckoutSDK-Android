package company.tap.checkout.internal.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.core.os.postDelayed
import company.tap.checkout.R
import company.tap.tapuilibrary.uikit.ktx.loadAppThemManagerFromPath
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.switch_layout.view.*


private var targetHeight: Int? = 0
private var animationDelayForResizeAnimation = 1500L
private var topLeftCorner = 16f
private var topRightCorner = 16f
private var bottomRightCorner = 0f
private var bottomLeftCorner = 0f

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

fun MutableList<View>.addFadeInAnimationForViews(durationTime: Long = 1000L) {
    this.forEachIndexed { index, view ->
        view.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.fade_in)
        animation.duration = durationTime
        view.startAnimation(animation)
    }

}

fun Context.twoThirdHeightView(): Double {
    return getDeviceSpecs().first.times(2.3) / 3
}

@SuppressLint("SetJavaScriptEnabled")
fun WebView.applyConfigurationForWebView(url: String, onProgressWebViewFinishedLoading: () -> Unit) {
    with(this) {
        settings.javaScriptEnabled = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.loadWithOverviewMode = true
        isVerticalScrollBarEnabled = true
        isHorizontalScrollBarEnabled = true
        setInitialScale(1)
        settings.defaultZoom = WebSettings.ZoomDensity.FAR;
        settings.useWideViewPort = true
        loadUrl(url)
        setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (this.canGoBack()) {
                    this.goBack()
                    /**
                     * put here listener or delegate thT process cancelled **/
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            false
        }

        this.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    onProgressWebViewFinishedLoading.invoke()
                }

            }
        }
    }


}

fun View.resizeAnimation(
    durationTime: Long = 1000L,
    startHeight: Int = 1000,
    endHeight:Int=1000,isExpanding:Boolean=false
) {
    val resizeAnimation = ResizeAnimation(
        this,
        endHeight,
        startHeight, isExpanding
    )
    resizeAnimation.duration = durationTime
    this.startAnimation(resizeAnimation)
}


/**
 * This function return specs of device height && width
 */
fun Context.getDeviceSpecs(): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()
    (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    val height = displayMetrics.heightPixels
    val width = displayMetrics.widthPixels
    val pair: Pair<Int, Int> = Pair(height, width)
    return pair
}

fun getViewShapeDrawable(
    isRoundedCorners: Boolean = false,
    shapeColor: String = AppColorTheme.MerchantHeaderViewColor
): Drawable {
    val shape = when (isRoundedCorners) {
        true ->
            ShapeDrawable(
                RoundRectShape(
                    floatArrayOf(
                        topLeftCorner, topLeftCorner,
                        topRightCorner, topRightCorner,
                        bottomRightCorner, bottomRightCorner,
                        bottomLeftCorner, bottomLeftCorner
                    ),
                    null, null
                )
            )
        false ->
            ShapeDrawable()
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        shape.colorFilter = BlendModeColorFilter(
            loadAppThemManagerFromPath(shapeColor),
            BlendMode.SRC_ATOP
        )
    }


    return shape
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

fun MutableList<View>.addFadeOutAnimationToViews(
    durationTime: Long = 500L,
    onAnimationEnd: () -> Unit?
) {
    this.forEachIndexed { index, view ->
//        val animation = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)
//        animation.duration = durationTime
//        view.startAnimation(animation)
        view.visibility = View.GONE
//        view.animation.setAnimationListener(object : AnimationListener {
//            override fun onAnimationStart(p0: Animation?) {
//            }
//
//            override fun onAnimationEnd(p0: Animation?) {
//                view.visibility = View.GONE
//                onAnimationEnd.invoke()
//
//            }
//
//            override fun onAnimationRepeat(p0: Animation?) {
//            }
//
//        })
    }


}
fun MutableList<View>.addFadeInAnimationToViews(durationTime: Long = 500L) {
    this.forEachIndexed { index, view ->

        val  animation = AnimationUtils.loadAnimation(view.context, R.anim.fade_in)
        animation.duration = durationTime
        view.startAnimation(animation)
        view.animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                view.visibility = View.VISIBLE


            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
    }


}





fun View.applyBluryToView(radiusNeeded: Int = 8, sampling: Int = 2, animationDuration: Int = 1000 , showOriginalView:Boolean = false) {
    Blurry.with(context).radius(radiusNeeded).sampling(sampling).animate(animationDuration)
        .onto(this as ViewGroup).apply {
            when(showOriginalView) {
               true-> this@applyBluryToView.getChildAt(0).visibility = View.VISIBLE
                false-> this@applyBluryToView.getChildAt(0).visibility = View.GONE
            }

        }


}

fun Context.showToast(message: String) {
    return Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        topLeftCorner, topLeftCorner,
        topRightCorner, topRightCorner,
        bottomRightCorner, bottomRightCorner,
        bottomLeftCorner, bottomLeftCorner
    )
    return gradientDrawable
}