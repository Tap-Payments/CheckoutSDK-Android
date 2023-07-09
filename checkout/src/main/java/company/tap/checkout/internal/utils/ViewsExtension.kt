package company.tap.checkout.internal.utils

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Rect
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.Visibility
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.*
import android.view.animation.Animation.AnimationListener
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.internal.enums.ThemeMode
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.AppColorTheme
import company.tap.tapuilibraryy.uikit.ktx.loadAppThemManagerFromPath
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.amountview_layout.view.*
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*


private var targetHeight: Int? = 0
private var animationDelayForResizeAnimation = 2000L
private var topLeftCorner = 45f
private var topRightCorner = 45f
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

fun View.addSlideUpAnimation(durationTime: Long = 1000L, onAnimationEnd: () -> Unit?) {
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

fun Context.isUserCurrencySameToMainCurrency(userCurrencySameAsCurrencyOfApplication: MutableLiveData<Boolean>): Boolean {
    val userCurrency = SharedPrefManager.getUserSupportedLocaleForTransactions(this)?.currency
    val paymentCurrency = PaymentDataSource.getCurrency()?.isoCode
    userCurrencySameAsCurrencyOfApplication.value =
        userCurrency.equals(paymentCurrency, ignoreCase = true)
    return userCurrencySameAsCurrencyOfApplication.value == true
}

fun View.slideFromStartToEnd() {
    val animate = TranslateAnimation(
        if (isRTL()) -this.width.toFloat() else this.width.toFloat(),
        0f,
        0f,
        0f
    ) // View for animation
    animate.duration = 1000
    this.startAnimation(animate)
    this.visibility = View.VISIBLE // Change visibility VISIBLE or GONE

}

fun Context.applyOnDifferentThemes(
    onDarkTheme: () -> Unit,
    onDarkColoredTheme: () -> Unit,
    onLightTheme: () -> Unit,
    onLightMonoTheme: () -> Unit
) {
    when (CustomUtils.getCurrentTheme()) {
        ThemeMode.dark.name -> {
            onDarkTheme.invoke()
        }
        ThemeMode.dark_colored.name -> {
            onDarkColoredTheme.invoke()
        }
        ThemeMode.light.name -> {
            onLightTheme.invoke()
        }
        ThemeMode.light_mono.name -> {
            onLightMonoTheme.invoke()

        }
    }
}


fun addShrinkageAnimationForViews(
    vararg views: View?,
    xDirection: Float = 0.95f,
    yDirection: Float = 1f,
    isDimmed: Boolean = false
) {
    views.forEach {
        it?.animate()?.scaleX(xDirection)?.scaleY(yDirection)?.setDuration(600)?.start();
        if (isDimmed) {
            it?.alpha = 0.4f
        } else {
            it?.alpha = 1.0f
        }
    }
}

fun View.addShrinkAnimation(xDirection: Float, yDirection: Float, isDimmed: Boolean = false) {
    this.animate().scaleX(xDirection).scaleY(yDirection).setDuration(600).start();
    if (isDimmed) {
        this.alpha = 0.4f
    } else {
        this.alpha = 1.0f
    }

}

fun ViewGroup.deepForEach(function: View.() -> Unit) {
    this.forEach { child ->
        child.function()
        if (child is ViewGroup) {
            child.deepForEach(function)
        }
    }
}

inline fun View.getDimensions(crossinline onDimensionsReady: (Int, Int) -> Unit) {
    lateinit var layoutListener: ViewTreeObserver.OnGlobalLayoutListener
    layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
        onDimensionsReady(width, height)
    }
    viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
}

inline fun View?.onSizeChange(
    crossinline destinationFunction: () -> Unit
) =
    this?.apply {
        addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Log.e("listen", "listen")
            val rect = Rect(left, top, right, bottom)
            val oldRect = Rect(oldLeft, oldTop, oldRight, oldBottom)
            if (rect.width() != oldRect.width() || rect.height() != oldRect.height()) {
                destinationFunction.invoke()
            }
        }
    }


/**
 * throttleLatest works similar to debounce but it operates on time intervals and
 * returns the latest data for each one, which allows you to get and process intermediate data if you need to
 */
fun throttleLatest(
    intervalMs: Long = 500L,
    coroutineScope: CoroutineScope,
    destinationFunction: () -> Unit
): () -> Unit {
    var throttleJob: Job? = null
    //  var latestParam: T
    return {
        //latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                destinationFunction.invoke()
            }
        }
    }
}

/**
 * throttleFirst is useful when you need to process the first call right away and then skip subsequent calls for
 * some time to avoid undesired behavior (avoid starting two identical activities on Android, for example).
 */

fun <T> throttleFirst(
    skipMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    return { param: T ->
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction(param)
                delay(skipMs)
            }
        }
    }
}

fun View.slideFromEndToStart() {

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


fun Context.twoThirdHeightView(): Double {
    return getDeviceSpecs().first.times(2.15) / 3
}

fun Context.twoThirdHeightViewWeb(): Double {
    return getDeviceSpecs().first.times(2.5) / 3
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
    endHeight: Int = 1000, isExpanding: Boolean = false, onAnimationStart: () -> Unit? = {}
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
 * first -> height
 * second -> width
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

fun View.addFadeOutAnimation(
    durationTime: Long = 500L,
    isGone: Boolean = true,
    onAnimationEnd: () -> Unit? = {}
) {
    if (this.isVisible) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animation.duration = durationTime
        this.startAnimation(animation)
        this.animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                if (isGone) this@addFadeOutAnimation.visibility = View.GONE
                else this@addFadeOutAnimation.visibility = View.INVISIBLE

                onAnimationEnd.invoke()
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
    }
}


fun MutableList<View>.addFadeOutAnimationToViews(
    durationTime: Long = 500L,
    headerLayout: LinearLayout? = null,
    onAnimationStart: () -> Unit? = {},
    onAnimationEnd: () -> Unit = {},
) {
    this.forEachIndexed { index, view ->
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)
        animation.duration = durationTime
        view.startAnimation(animation)
        view.animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                onAnimationStart.invoke()
            }

            override fun onAnimationEnd(p0: Animation?) {
                view.visibility = View.GONE
                headerLayout?.removeView(view)
                onAnimationEnd.invoke()


            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
    }


}

fun View.slideView(
    newHeight: Int
) {

    val slideAnimator = ValueAnimator
        .ofInt(if (this.measuredHeight == newHeight) 0 else this.measuredHeight, newHeight)
        .setDuration(600)

    slideAnimator.addUpdateListener {
        this.layoutParams.height = it.animatedValue as Int
        this.requestLayout()
    }

    /*  We use an animationSet to play the animation  */
    val animationSet = AnimatorSet();
    animationSet.interpolator = DecelerateInterpolator();
    animationSet.play(slideAnimator);
    animationSet.start()
}

fun animateBS(
    changeHeight: () -> Unit,
    fromView: ViewGroup,
    toView: ViewGroup,
    transitionAnimation: Long = 500L,
    onTransitionEnd: () -> Unit = {}
) {
    val transition = AutoTransition()
    transition.addTarget(fromView)
    transition.interpolator = FastOutSlowInInterpolator()
    transition.duration = transitionAnimation
    androidx.transition.TransitionManager.beginDelayedTransition(toView, transition)
    changeHeight()
    androidx.transition.TransitionManager.endTransitions(fromView)
    onTransitionEnd.invoke()
}
fun View.isVisibileWithAnimation(isVisible:Boolean){
    when(isVisible){
        true ->{
            doAfterSpecificTime(time = 3000L) {
                fadeVisibility(View.VISIBLE)
            }
        }
        false ->fadeVisibility(View.GONE)
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

    (this as ViewGroup).children.forEachIndexed { index, view ->
        if (index != 0) removeView(view)
    }

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

fun MutableList<View>.disableViews() {
    this.forEachIndexed { index, view ->
        view.isEnabled = false
        view.isClickable = false
        view.isActivated = false

    }
}

fun RecyclerView.scrollToFirstPosition() = this.post {
    apply { scrollToPosition(0) }
}

fun MutableList<View>.enableViews() {
    this.forEachIndexed { index, view ->
        view.isEnabled = true
        view.isClickable = true
        view.isActivated = true

    }
}