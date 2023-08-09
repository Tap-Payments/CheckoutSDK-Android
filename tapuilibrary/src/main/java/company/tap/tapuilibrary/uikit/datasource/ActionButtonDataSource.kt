package company.tap.tapuilibrary.uikit.datasource

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

/**
 *
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
data class ActionButtonDataSource(
    val text: String? = null,
    val textSize: Float? = null,
    val cornerRadius: Float? = null,
    val successImage: Drawable? = null,
    val isActivated: Boolean? = false,

    @ColorInt
    val textColor: Int? = null,

    @ColorRes
    val textColorResources: Int? = null,

    @DrawableRes
    val successImageResources: Int? = null,

    @ColorInt
    val errorColor: Int? = null,

    @ColorRes
    val errorColorResources: Int? = null,

    val errorImage: Drawable? = null,

    @DrawableRes
    val errorImageResources: Int? = null,

    @ColorInt
    var backgroundColor: Int? = null,

    @ColorRes
    val backgroundColorResources: Int? = null,

    @ColorRes
   val backgroundArrayInt: IntArray? = null
)