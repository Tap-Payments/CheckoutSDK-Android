package company.tap.checkout.internal.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.animation.*
import android.view.animation.Animation.AnimationListener
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.ktx.loadAppThemManagerFromPath
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.amountview_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import java.util.*


private var targetHeight: Int? = 0
private var animationDelayForResizeAnimation = 2000L
private var topLeftCorner = 16f
private var topRightCorner = 16f
private var bottomRightCorner = 0f
private var bottomLeftCorner = 0f
const val progressBarSize = 45

fun View.startPoweredByAnimation(
    delayTime: Long,
    poweredByLogo: View?,
    onAnimationEnd: () -> Unit?
) {
    Handler(Looper.getMainLooper()).postDelayed({
        poweredByLogo?.visibility = View.GONE
        this.visibility = View.VISIBLE
        this.addSlideUpAnimation(onAnimationEnd = onAnimationEnd)
        doAfterSpecificTime(execute = {
            poweredByLogo?.addFadeInAnimation()
        })
    }, delayTime)

}

fun Context?.getDimensionsInDp(dimension: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dimension.toFloat(),
        this?.resources?.displayMetrics
    ).toInt()

}

fun doAfterSpecificTime(time: Long = 1000L, execute: () -> Unit) =
    Handler(Looper.getMainLooper()).postDelayed(time) {
        execute.invoke()
    }


fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = this.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        this.requestLayout()
    }
}

fun View.applyGlowingEffect(colorPairs: Pair<Int, Int>, durationTime: Long = 1000L) {
    val backgroundColor = "BackgroundColor"
    val animator: ObjectAnimator =
        ObjectAnimator.ofInt(
            this,
            backgroundColor,
            colorPairs.first,
            colorPairs.second
        ).setDuration(durationTime)

    this.setMargins(0, 0, 0, 0)
    animator.setEvaluator(ArgbEvaluator())
    animator.repeatMode = ValueAnimator.REVERSE
    animator.repeatCount = Animation.INFINITE
    animator.start()


}

fun View.addFadeInAnimation(durationTime: Long = 1000L) {
    this.visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    animation.duration = durationTime
    this.startAnimation(animation)
}

fun View.addSlideUpAnimation( durationTime: Long = 1000L,onAnimationEnd: () -> Unit?) {
    val animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
    animation.duration = durationTime
    this.startAnimation(animation)
    this.animation.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            onAnimationEnd.invoke()
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    })
}

fun Context.isUserCurrencySameToMainCurrency(): Boolean {
    val userCurrency = SharedPrefManager.getUserSupportedLocaleForTransactions(this)?.currency
    val paymentCurrency = PaymentDataSource.getCurrency()?.isoCode
    return userCurrency.equals(paymentCurrency, ignoreCase = true)
}

fun View.slidefromRightToLeft() {
    val animate = TranslateAnimation(
        if (isRTL()) -this.width.toFloat() else this.width.toFloat(),
        0f,
        0f,
        0f
    ) // View for animation
    animate.duration = 1000
    animate.fillAfter = true
    this.startAnimation(animate)
    this.visibility = View.VISIBLE // Change visibility VISIBLE or GONE

}

fun View.slideFromLeftToRight() {

    if (this.isVisible) {
        val animate =
            TranslateAnimation(
                0f,
                if (isRTL()) -this.width.toFloat() else this.width.toFloat(),
                0f,
                0f
            ) // View for animation
        animate.duration = 1000
        animate.fillAfter = false
        this.startAnimation(animate)
        this.visibility = View.GONE // Change visibility VISIBLE or GONE
    }

}

fun View.isRTL() = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL




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
fun WebView.applyConfigurationForWebView(
    url: String,
    onProgressWebViewFinishedLoading: () -> Unit
) {
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
    endHeight: Int = 1000, isExpanding: Boolean = false,onAnimationStart: () -> Unit? = {}
) {
    val resizeAnimation = ResizeAnimation(
        this,
        endHeight,
        startHeight, isExpanding
    )
    resizeAnimation.duration = durationTime
    this.startAnimation(resizeAnimation)
    this.animation.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            onAnimationStart.invoke()
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    })
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

fun View.addFadeOutAnimation(durationTime: Long = 500L,isGone :Boolean=true) {
    if (this.isVisible) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animation.duration = durationTime
        this.startAnimation(animation)
        this.animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                if (isGone)this@addFadeOutAnimation.visibility = View.GONE
               else  this@addFadeOutAnimation.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
    }
}

fun View.addNewFadeOut(durationTime: Long = 500L) {
    if (this.isVisible) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator() //and this
        fadeOut.startOffset = 1000
        fadeOut.duration = 1000
        val animation = AnimationSet(false) //change to false
        animation.addAnimation(fadeOut)
        this.animation = animation
        this.startAnimation(animation)

    }
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

        val animation = AnimationUtils.loadAnimation(view.context, R.anim.fade_in)
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


fun View.applyBluryToView(
    radiusNeeded: Int = 8,
    sampling: Int = 2,
    animationDuration: Int = 1000,
    showOriginalView: Boolean = false
) {
    Blurry.with(context).radius(radiusNeeded).sampling(sampling).animate(animationDuration)
        .onto(this as ViewGroup).apply {
            when (showOriginalView) {
                true -> this@applyBluryToView.getChildAt(0).visibility = View.VISIBLE
                false -> this@applyBluryToView.getChildAt(0).visibility = View.GONE
            }

        }


}


fun ViewGroup.addLoaderWithBlurryToView(invokeAfterLoad: () -> Unit, viewToBeBLur: View?) {

    @DrawableRes
    val loaderGif: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            R.drawable.loader
        } else if (ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains(
                "dark"
            )
        ) {
            R.drawable.output_black_loader_nobg
        } else R.drawable.loader



    viewToBeBLur?.applyBluryToView()
    val progressImage = ImageView(context)
    val params = FrameLayout.LayoutParams(progressBarSize, progressBarSize)
    params.gravity = Gravity.CENTER
    progressImage.layoutParams = params
    Glide.with(context).asGif().load(loaderGif).into(progressImage)
    this.addView(progressImage)
    doAfterSpecificTime(2000) {
        invokeAfterLoad.invoke()
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
fun View.fadeVisibility(visibility: Int, duration: Long = 400) {
    val transition: Transition = Fade()
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    this.visibility = visibility
}