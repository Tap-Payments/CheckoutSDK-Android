package company.tap.tapuilibrary.uikit.datasource

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
data class GoPayLoginDataSource(
    val emailTabText: String? = null,
    val emailInputHint: String? = null,

    val phoneTabText: String? = null,
    val phoneInputHint: String? = null,
    val goPayHintText: String? = null,

    val emailImageURL: String? = null,
    val phoneImageURL: String? = null,

    @DrawableRes
    val emailImageResource: Int? = null,
    @DrawableRes
    val phoneImageResource: Int? = null,

    val phoneImageDrawable: Drawable? = null,
    val emailImageDrawable: Drawable? = null
)