package company.tap.tapuilibraryy.uikit.models

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import company.tap.tapcardvalidator_android.CardBrand

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
data class SectionTabItem(
    val selectedImageURL: String,
    val unSelectedImage: String,
    val type: CardBrand,
    val disabledImageUrl: String?=null,
    var imageView: ImageView? = null,
    var indicator: View? = null
)