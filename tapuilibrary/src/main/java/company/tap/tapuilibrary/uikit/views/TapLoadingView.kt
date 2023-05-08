package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.uikit.interfaces.TapProgressIndicatorInterface
import company.tap.tapuilibrary.uikit.ktx.setImage


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

    init {
        inflate(context, R.layout.tap_loading_view, this)
        tapLoadingImage = findViewById(R.id.tapLoadingImage)
        if (context != null) {
            tapLoadingImage.setImage(context,tapLoadingImage,R.drawable.loader,1) {onProgressEnd()}
        }
    }

    fun setOnProgressCompleteListener(onProgressCompletedListener: OnProgressCompletedListener) {
        this.onProgressCompletedListener = onProgressCompletedListener
    }

    fun completeProgress() {
     //   onProgressEnd()
        tapLoadingImage.setImage(context,tapLoadingImage,R.drawable.loader,1) {
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