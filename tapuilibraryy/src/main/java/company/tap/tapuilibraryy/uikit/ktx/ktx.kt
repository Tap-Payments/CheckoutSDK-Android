package company.tap.tapuilibraryy.uikit.ktx

import android.R.attr.path
import android.R.attr.radius
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.themekit.ThemeManager


/**
 * Created by OLAMONIR on 7/16/20.

Copyright (c) 2020  Tap Payments.
All rights reserved.
 **/

fun ImageView.setImage(
    context: Context,
    image: ImageView,
    imageRes: Int,
    gifLoopCount: Int,
    actionAfterAnimationDone: () -> Unit
): ImageView {
    if (!(context as Activity).isDestroyed)
        Glide.with(this).asGif().load(imageRes).useAnimationPool(true).listener(object :
            RequestListener<GifDrawable> {
            override fun onResourceReady(
                resource: GifDrawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                if (resource is GifDrawable) {
                    resource.setLoopCount(gifLoopCount)
                    resource.registerAnimationCallback(object :
                        Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable) {
                            //do whatever after specified number of loops complete
                            actionAfterAnimationDone()

                        }
                    })
                }
                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }).into(image)
    return image
}


/**
 * Method to draw bordered view
 * setBorderedView ( view: View, cornerRadius:Float,strokeWidth: Float, strokeColor: Int,tintColor: Int )
 */

fun setBorderedView(
    view: View,
    cornerRadius: Float,
    strokeWidth: Float,
    strokeColor: Int,
    tintColor: Int,
    shadowColor: Int
) {
    val shapeAppearanceModel = ShapeAppearanceModel()
        .toBuilder()
        .setAllCorners(CornerFamily.ROUNDED, cornerRadius)
        .build()
    val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
    ViewCompat.setBackground(view, shapeDrawable)
    shapeDrawable.setStroke(strokeWidth, strokeColor)
    shapeDrawable.setShadowColor(shadowColor)
    shapeDrawable.setTint(tintColor)
    shapeDrawable.shadowRadius = 10
    shapeDrawable.elevation = 20f
}


fun setTopBorders(
    view: View,
    cornerRadius: Float = 40f,
    strokeWidth: Float = 0f,
    strokeColor: Int = view.resources.getColor(R.color.colorBackground),
    tintColor: Int = view.resources.getColor(R.color.colorBackground),
    shadowColor: Int = view.resources.getColor(R.color.colorBackground)
) {
    val shapeAppearanceModel = ShapeAppearanceModel()
        .toBuilder()
        .setTopLeftCorner(CornerFamily.ROUNDED, cornerRadius)
        .setTopRightCorner(CornerFamily.ROUNDED, cornerRadius)
        .build()
    val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
    ViewCompat.setBackground(view, shapeDrawable)
    shapeDrawable.setStroke(strokeWidth, strokeColor)
    shapeDrawable.setShadowColor(shadowColor)
    shapeDrawable.setTint(tintColor)
    shapeDrawable.shadowRadius = 10
    shapeDrawable.elevation = 20f
}

fun loadAppThemManagerFromPath(pathValue: String) =  Color.parseColor(ThemeManager.getValue(pathValue))

fun setBottomBorders(
    view: View,
    cornerRadius: Float,
    strokeWidth: Float,
    strokeColor: Int,
    tintColor: Int,
    shadowColor: Int
) {
    val shapeAppearanceModel = ShapeAppearanceModel()
        .toBuilder()
        .setBottomLeftCorner(CornerFamily.ROUNDED, cornerRadius)
        .setBottomRightCorner(CornerFamily.ROUNDED, cornerRadius)
        .build()
    val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
    ViewCompat.setBackground(view, shapeDrawable)
    shapeDrawable.setStroke(strokeWidth, strokeColor)
    shapeDrawable.setShadowColor(shadowColor)
    shapeDrawable.setTint(tintColor)
    shapeDrawable.shadowRadius = 10
    shapeDrawable.elevation = 20f
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color =
                    Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.subTitleTextColor"))
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        if (startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}
















