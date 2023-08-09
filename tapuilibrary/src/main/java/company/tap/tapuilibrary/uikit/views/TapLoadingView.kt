package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.interfaces.TapProgressIndicatorInterface
import company.tap.tapuilibrary.uikit.ktx.setImage
import company.tap.tapuilibrary.R


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLoadingView(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs) ,
    TapProgressIndicatorInterface {

    var tapLoadingImage: ImageView
    var onProgressCompletedListener: OnProgressCompletedListener? = null
    @DrawableRes
    val loaderGif: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")){
            R.drawable.loader_black
        }  else if (ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains("dark")){
            R.drawable.loader
        }else    R.drawable.loader
    init {
        inflate(context, R.layout.tap_loading_view, this)
        tapLoadingImage = findViewById(R.id.tapLoadingImage)
        if (context != null) {
            tapLoadingImage.setImage(context,tapLoadingImage,loaderGif,1) {onProgressEnd()}
        }
    }

    fun setOnProgressCompleteListener(onProgressCompletedListener: OnProgressCompletedListener) {
        this.onProgressCompletedListener = onProgressCompletedListener
    }

    fun completeProgress() {
     //   onProgressEnd()
        tapLoadingImage.setImage(context,tapLoadingImage,loaderGif,1) {
            onProgressEnd()
        }
    }
    override fun onProgressEnd() {
        onProgressCompletedListener?.onProgressCompleted()
    }

    interface OnProgressCompletedListener {
        fun onProgressCompleted()
    }
}